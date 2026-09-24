package com.example.ai

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.LineNarrationConfig
import com.example.data.model.PoetryAnalysisResult
import com.example.data.model.PronunciationTip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

class GeminiPoetryService(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Model mandated by prompt requirements
    private val ttsModel = "gemini-3.8-flash-tts"
    private val textAnalysisModel = "gemini-3.5-flash"

    /**
     * Synthesizes emotionally expressive male voice narration using gemini-3.8-flash-tts.
     */
    suspend fun generateExpressiveNarration(
        text: String,
        voiceName: String, // Prebuilt Gemini voice (Charon, Fenrir, Orus, Puck, Zephyr)
        emotionDirective: String,
        speedMultiplier: Float = 1.0f,
        voiceDepth: Float = 1.0f,
        outputFileName: String = "narration_${System.currentTimeMillis()}.wav"
    ): Result<File> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("GEMINI_API_KEY is not configured. Please add your key to the Secrets panel.")
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$ttsModel:generateContent?key=$apiKey"

            // Construct dramatic performance prompt with rich emotional and cadence directives
            val performancePrompt = buildString {
                append("Recite this Hindi/Urdu poetry as a master classical poet, shayar, and emotional voice artist.\n")
                append("Voice style: Deep resonant male voice.\n")
                append("Performance Direction: $emotionDirective\n")
                append("Pacing: ")
                if (speedMultiplier < 0.95f) append("Slow, deliberate, allowing each metaphor and word to breathe. ")
                else if (speedMultiplier > 1.05f) append("Energetic, rhythmic, stirring. ")
                else append("Natural measured poetic cadence. ")
                append("Maintain pristine, authentic pronunciation of Urdu nuqta sounds (ख़, ग़, क़, ज़, फ़, ژ).\n")
                append("Poem lines to perform:\n\n")
                append(text)
            }

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", performancePrompt) })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val generationConfig = JSONObject().apply {
                    put("responseModalities", JSONArray().apply { put("AUDIO") })
                    val speechConfig = JSONObject().apply {
                        val voiceConfig = JSONObject().apply {
                            val prebuiltVoiceConfig = JSONObject().apply {
                                put("voiceName", voiceName)
                            }
                            put("prebuiltVoiceConfig", prebuiltVoiceConfig)
                        }
                        put("voiceConfig", voiceConfig)
                    }
                    put("speechConfig", speechConfig)
                }
                put("generationConfig", generationConfig)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e("GeminiPoetryService", "TTS API error: ${response.code} - $responseString")
                return@withContext Result.failure(Exception("TTS API call failed: ${response.code} - $responseString"))
            }

            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            var rawBase64Audio: String? = null
            var mimeType = "audio/wav"

            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    val inlineData = part.optJSONObject("inlineData")
                    if (inlineData != null) {
                        rawBase64Audio = inlineData.optString("data")
                        mimeType = inlineData.optString("mimeType", "audio/wav")
                        break
                    }
                }
            }

            if (rawBase64Audio.isNullOrBlank()) {
                return@withContext Result.failure(Exception("No audio data returned by Gemini voice service"))
            }

            val audioBytes = Base64.decode(rawBase64Audio, Base64.DEFAULT)
            val cacheDir = File(context.cacheDir, "narrations").apply { mkdirs() }
            val outputFile = File(cacheDir, outputFileName)

            // If audio is raw PCM or needs standard WAV header
            if (mimeType.contains("pcm") || (!mimeType.contains("mp3") && !isWavFile(audioBytes))) {
                val wavBytes = createWavFromPcm(audioBytes, sampleRate = 24000, channels = 1)
                FileOutputStream(outputFile).use { it.write(wavBytes) }
            } else {
                FileOutputStream(outputFile).use { it.write(audioBytes) }
            }

            Result.success(outputFile)
        } catch (e: Exception) {
            Log.e("GeminiPoetryService", "Failed to generate narration", e)
            Result.failure(e)
        }
    }

    /**
     * Automatic Poetry Interpretation & Emotional Analysis.
     */
    suspend fun analyzePoetry(poemText: String): Result<PoetryAnalysisResult> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Return intelligent heuristic analysis if no key configured
            return@withContext Result.success(createHeuristicAnalysis(poemText))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$textAnalysisModel:generateContent?key=$apiKey"

            val prompt = """
                Analyze this Hindi / Urdu poem or shayari for emotional narration.
                Return ONLY valid JSON matching this schema:
                {
                  "detectedMood": "Melancholic & Reflective",
                  "detectedEmotionId": "heartbreak", // choose from: love, romance, sadness, heartbreak, longing, nostalgia, hope, anger, pain, loneliness, happiness, devotion, mystery, intensity, peace, reflection
                  "emotionalIntensity": 1.4, // float from 0.5 to 2.0
                  "rhythmicStructure": "Traditional Behr / Couplets",
                  "meaningSummary": "A poignant reflection on heartbreak...",
                  "recommendedVoiceId": "deep_emotional", // choose from: deep_emotional, soft_romantic, mature_poet, deep_cinematic, calm_storyteller, melancholic_sad, passionate_poetic, powerful_dramatic, gentle_urdu_shayar, classical_ghazal, natural_conversational, warm_intimate
                  "recommendedAmbienceId": "melancholy_cello", // choose from: romantic_piano, melancholy_cello, sufi_tanpura, late_night_rain, cinematic_atmosphere, ethereal_ambient, classical_sarod, minimal_piano, dark_ambient
                  "pronunciationTips": [
                     {"word": "ख़्वाब", "phoneticUrduHindi": "Khwaab (गले से ख़)", "note": "Pronounce uvular voiceless fricative 'kh' with nuqta"}
                  ],
                  "dramaticLineIndices": [1, 3]
                }

                Poem:
                $poemText
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val generationConfig = JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.4)
                }
                put("generationConfig", generationConfig)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@withContext Result.success(createHeuristicAnalysis(poemText))
            }

            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates")
            val candidateText = candidates?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text").orEmpty()

            if (candidateText.isBlank()) {
                return@withContext Result.success(createHeuristicAnalysis(poemText))
            }

            val parsedJson = JSONObject(candidateText)
            val tips = mutableListOf<PronunciationTip>()
            val tipsArray = parsedJson.optJSONArray("pronunciationTips")
            if (tipsArray != null) {
                for (i in 0 until tipsArray.length()) {
                    val item = tipsArray.getJSONObject(i)
                    tips.add(
                        PronunciationTip(
                            word = item.optString("word"),
                            phoneticUrduHindi = item.optString("phoneticUrduHindi"),
                            note = item.optString("note")
                        )
                    )
                }
            }

            val dramaticIndices = mutableListOf<Int>()
            val indicesArray = parsedJson.optJSONArray("dramaticLineIndices")
            if (indicesArray != null) {
                for (i in 0 until indicesArray.length()) {
                    dramaticIndices.add(indicesArray.getInt(i))
                }
            }

            val result = PoetryAnalysisResult(
                detectedMood = parsedJson.optString("detectedMood", "Emotional & Poetic"),
                detectedEmotionId = parsedJson.optString("detectedEmotionId", "heartbreak"),
                emotionalIntensity = parsedJson.optDouble("emotionalIntensity", 1.2).toFloat(),
                rhythmicStructure = parsedJson.optString("rhythmicStructure", "Couplets"),
                meaningSummary = parsedJson.optString("meaningSummary", "Classic Hindi/Urdu poetic expression."),
                pronunciationTips = tips,
                recommendedVoiceId = parsedJson.optString("recommendedVoiceId", "deep_emotional"),
                recommendedAmbienceId = parsedJson.optString("recommendedAmbienceId", "melancholy_cello"),
                dramaticLineIndices = dramaticIndices
            )
            Result.success(result)
        } catch (e: Exception) {
            Log.e("GeminiPoetryService", "Poetry analysis error", e)
            Result.success(createHeuristicAnalysis(poemText))
        }
    }

    private fun createHeuristicAnalysis(text: String): PoetryAnalysisResult {
        val lower = text.lowercase()
        val isSad = lower.contains("दर्द") || lower.contains("ग़म") || lower.contains("اشک") || lower.contains("موت") || lower.contains("ماتم") || lower.contains("शिकस्त")
        val isLove = lower.contains("इश्क़") || lower.contains("मोहब्बत") || lower.contains("عشق") || lower.contains("محبت") || lower.contains("यार") || lower.contains("महबूब")
        val isInquilab = lower.contains("ख़ून") || lower.contains("आग") || lower.contains("इंक़िलाब") || lower.contains("ہندوستان") || lower.contains("جنگ")
        val isSufi = lower.contains("ख़ुदा") || lower.contains("इलाही") || lower.contains("رَب") || lower.contains("सूफ़ी")

        val (emotionId, voiceId, ambienceId) = when {
            isInquilab -> Triple("intensity", "powerful_dramatic", "cinematic_atmosphere")
            isSufi -> Triple("devotion", "classical_ghazal", "sufi_tanpura")
            isSad -> Triple("heartbreak", "deep_emotional", "melancholy_cello")
            isLove -> Triple("romance", "soft_romantic", "romantic_piano")
            else -> Triple("reflection", "mature_poet", "late_night_rain")
        }

        val tips = mutableListOf<PronunciationTip>()
        if (text.contains("ख़") || text.contains("خ")) {
            tips.add(PronunciationTip("ख़ (Kh)", "Grave 'Kh'", "Pronounce uvular voiceless fricative softly from the epiglottis."))
        }
        if (text.contains("ग़") || text.contains("غ")) {
            tips.add(PronunciationTip("ग़ (Gh)", "Voiced 'Gh'", "Pronounce throat sound softly vibrating."))
        }
        if (text.contains("क़") || text.contains("ق")) {
            tips.add(PronunciationTip("क़ (Qaf)", "Deep 'Q'", "Deep uvular stop pronounced at the very back of the mouth."))
        }

        return PoetryAnalysisResult(
            detectedMood = if (isSad) "Poignant Sorrow & Separation" else if (isLove) "Tender Longing & Romance" else "Contemplative Poetic Depth",
            detectedEmotionId = emotionId,
            emotionalIntensity = 1.3f,
            rhythmicStructure = "Sher-o-Shayari Couplet Matrix",
            meaningSummary = "Profound thematic reflection on inner states, poetic dialogue, and the human heart.",
            pronunciationTips = tips,
            recommendedVoiceId = voiceId,
            recommendedAmbienceId = ambienceId,
            dramaticLineIndices = listOf(0, 2)
        )
    }

    private fun isWavFile(data: ByteArray): Boolean {
        if (data.size < 12) return false
        val riff = String(data, 0, 4)
        val wave = String(data, 8, 4)
        return riff == "RIFF" && wave == "WAVE"
    }

    private fun createWavFromPcm(pcmData: ByteArray, sampleRate: Int = 24000, channels: Int = 1): ByteArray {
        val header = ByteArray(44)
        val totalDataLen = pcmData.size + 36
        val byteRate = sampleRate * channels * 2

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // Audio format 1 = PCM
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * 2).toByte()
        header[33] = 0
        header[34] = 16 // Bits per sample
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (pcmData.size and 0xff).toByte()
        header[41] = ((pcmData.size shr 8) and 0xff).toByte()
        header[42] = ((pcmData.size shr 16) and 0xff).toByte()
        header[43] = ((pcmData.size shr 24) and 0xff).toByte()

        val fullWav = ByteArray(header.size + pcmData.size)
        System.arraycopy(header, 0, fullWav, 0, header.size)
        System.arraycopy(pcmData, 0, fullWav, header.size, pcmData.size)
        return fullWav
    }
}
