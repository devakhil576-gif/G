package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.AmbienceCatalog
import com.example.data.model.BackgroundAmbience
import com.example.data.model.WaveformType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val totalDurationMs: Long = 0L,
    val currentLineIndex: Int = 0,
    val isDucking: Boolean = false,
    val isGeneratingVoice: Boolean = false
)

class AudioEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var mediaPlayer: MediaPlayer? = null
    private var musicAudioTrack: AudioTrack? = null
    private var isMusicPlaying = false
    private var musicJob: Job? = null

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    // Mixer properties
    var voiceVolume: Float = 0.95f
        set(value) {
            field = value.coerceIn(0f, 1.5f)
            mediaPlayer?.setVolume(field.coerceAtMost(1.0f), field.coerceAtMost(1.0f))
        }

    var musicVolume: Float = 0.38f
        set(value) {
            field = value.coerceIn(0f, 1.0f)
            updateMusicVolume()
        }

    var ambientVolume: Float = 0.30f
        set(value) {
            field = value.coerceIn(0f, 1.0f)
        }

    var reverbAmount: Float = 0.35f
    var autoDucking: Boolean = true

    private var currentAmbience: BackgroundAmbience = AmbienceCatalog.ambiences.first()
    private var currentNarrationFile: File? = null
    private var lineTimings: List<Pair<Long, Long>> = emptyList() // Pair(startMs, endMs)

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error initializing TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val resultHindi = tts?.setLanguage(Locale("hi", "IN"))
            if (resultHindi == TextToSpeech.LANG_MISSING_DATA || resultHindi == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.getDefault())
            }
            isTtsReady = true
        }
    }

    fun setBackgroundAmbience(ambience: BackgroundAmbience) {
        currentAmbience = ambience
        if (isMusicPlaying) {
            startAmbienceLoop()
        }
    }

    fun setNarrationFile(file: File, lineCount: Int, estimatedLineDurationMs: Long = 3500L) {
        currentNarrationFile = file
        mediaPlayer?.release()
        mediaPlayer = null

        // Generate line timings
        val timings = mutableListOf<Pair<Long, Long>>()
        var currentStart = 0L
        for (i in 0 until lineCount) {
            val end = currentStart + estimatedLineDurationMs
            timings.add(Pair(currentStart, end))
            currentStart = end
        }
        lineTimings = timings
    }

    fun playPerformance(onLineChanged: (Int) -> Unit = {}) {
        val file = currentNarrationFile
        if (file != null && file.exists()) {
            playWithMediaPlayer(file, onLineChanged)
        } else {
            // No file yet
            startAmbienceLoop()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
        }
    }

    private fun playWithMediaPlayer(file: File, onLineChanged: (Int) -> Unit) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setVolume(voiceVolume.coerceAtMost(1.0f), voiceVolume.coerceAtMost(1.0f))
                setOnCompletionListener {
                    stopAll()
                }
                start()
            }

            startAmbienceLoop()

            val duration = mediaPlayer?.duration?.toLong() ?: 0L
            _playbackState.value = _playbackState.value.copy(
                isPlaying = true,
                totalDurationMs = duration,
                currentPositionMs = 0L
            )

            // Launch tracker loop
            scope.launch {
                while (mediaPlayer?.isPlaying == true) {
                    val pos = mediaPlayer?.currentPosition?.toLong() ?: 0L
                    var activeLine = 0
                    for (i in lineTimings.indices) {
                        val (start, end) = lineTimings[i]
                        if (pos in start..end) {
                            activeLine = i
                            break
                        }
                    }

                    _playbackState.value = _playbackState.value.copy(
                        currentPositionMs = pos,
                        currentLineIndex = activeLine,
                        isDucking = autoDucking
                    )
                    withContext(Dispatchers.Main) {
                        onLineChanged(activeLine)
                    }
                    delay(80)
                }
            }
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error playing narration", e)
        }
    }

    fun pause() {
        mediaPlayer?.pause()
        stopAmbience()
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    fun resume(onLineChanged: (Int) -> Unit = {}) {
        mediaPlayer?.start()
        startAmbienceLoop()
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.seekTo(positionMs.toInt())
        _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
    }

    fun stopAll() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error stopping player", e)
        }
        stopAmbience()
        tts?.stop()
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            currentPositionMs = 0L,
            currentLineIndex = 0
        )
    }

    /**
     * Preview single voice using Android TTS (immediate offline fallback).
     */
    fun previewVoiceOffline(text: String, pitch: Float = 1.0f, speed: Float = 0.9f) {
        if (!isTtsReady || tts == null) return
        tts?.setPitch(pitch)
        tts?.setSpeechRate(speed)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "preview_utterance")
    }

    private fun startAmbienceLoop() {
        stopAmbience()
        isMusicPlaying = true

        val sampleRate = 22050
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        musicAudioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 4)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        updateMusicVolume()
        musicAudioTrack?.play()

        musicJob = scope.launch(Dispatchers.Default) {
            val chunkLengthSeconds = 2.0
            val numSamples = (sampleRate * chunkLengthSeconds).toInt()
            var phaseAcc = 0.0

            while (isMusicPlaying && isActive) {
                val buffer = ShortArray(numSamples)
                val baseFreqs = currentAmbience.baseFrequencies

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    var sampleVal = 0.0

                    when (currentAmbience.waveformType) {
                        WaveformType.WARM_PIANO_PAD -> {
                            // Warm lush additive harmonic pads
                            for ((idx, f) in baseFreqs.withIndex()) {
                                val detune = (idx * 0.4)
                                sampleVal += sin(2 * PI * (f + detune) * (t + phaseAcc)) * 0.25
                                sampleVal += sin(4 * PI * f * (t + phaseAcc)) * 0.08
                            }
                            // Breathing slow modulation
                            val lfo = 0.8 + 0.2 * sin(2 * PI * 0.25 * (t + phaseAcc))
                            sampleVal *= lfo
                        }
                        WaveformType.SUFI_TANPURA_DRONE -> {
                            // Rich Indian Sa-Pa drone overtone series
                            for (f in baseFreqs) {
                                sampleVal += sin(2 * PI * f * (t + phaseAcc)) * 0.3
                                sampleVal += sin(2 * PI * f * 2.0 * (t + phaseAcc)) * 0.15
                                sampleVal += sin(2 * PI * f * 3.0 * (t + phaseAcc)) * 0.08
                                sampleVal += sin(2 * PI * f * 4.0 * (t + phaseAcc)) * 0.04
                            }
                            // Gentle jawari buzz
                            val buzz = (sin(2 * PI * 12.0 * (t + phaseAcc)) * 0.05)
                            sampleVal += buzz
                        }
                        WaveformType.MELLOW_CELLO -> {
                            // Warm bowed cello harmonics
                            for (f in baseFreqs) {
                                sampleVal += sin(2 * PI * f * (t + phaseAcc)) * 0.35
                                sampleVal += sin(2 * PI * f * 3.0 * (t + phaseAcc)) * 0.12
                                sampleVal += sin(2 * PI * f * 5.0 * (t + phaseAcc)) * 0.05
                            }
                            // Vibrato
                            val vib = 1.0 + 0.015 * sin(2 * PI * 4.5 * (t + phaseAcc))
                            sampleVal *= vib
                        }
                        WaveformType.RAIN_AND_ATMOSPHERE -> {
                            // Soft filtered pinkish noise for rain + subtle harmonic bell
                            val noise = (Math.random() * 2.0 - 1.0) * 0.12
                            val bell = sin(2 * PI * baseFreqs.first() * (t + phaseAcc)) * 0.15
                            sampleVal = noise + bell
                        }
                        WaveformType.CINEMATIC_SUB -> {
                            // Deep sub bass + low rumble
                            val sub = sin(2 * PI * (baseFreqs.first() * 0.5) * (t + phaseAcc)) * 0.4
                            val rumble = sin(2 * PI * baseFreqs.first() * (t + phaseAcc)) * 0.25
                            sampleVal = sub + rumble
                        }
                        else -> {
                            for (f in baseFreqs) {
                                sampleVal += sin(2 * PI * f * (t + phaseAcc)) * 0.2
                            }
                        }
                    }

                    // Soft clipper
                    val clipped = (sampleVal.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
                    buffer[i] = clipped
                }

                phaseAcc += chunkLengthSeconds
                musicAudioTrack?.write(buffer, 0, numSamples)
            }
        }
    }

    private fun updateMusicVolume() {
        val effectiveVolume = if (autoDucking && mediaPlayer?.isPlaying == true) {
            musicVolume * 0.45f // Duck by 55% during voice narration
        } else {
            musicVolume
        }
        musicAudioTrack?.setVolume(effectiveVolume)
    }

    private fun stopAmbience() {
        isMusicPlaying = false
        musicJob?.cancel()
        musicJob = null
        try {
            musicAudioTrack?.pause()
            musicAudioTrack?.flush()
            musicAudioTrack?.release()
            musicAudioTrack = null
        } catch (e: Exception) {
            Log.e("AudioEngine", "Error releasing music track", e)
        }
    }

    /**
     * Exports combined narration + synthesized background ambience as a high-fidelity WAV file.
     */
    suspend fun exportMasterAudio(
        narrationFile: File?,
        ambience: BackgroundAmbience,
        outputFileName: String = "Sukhan_Performance_${System.currentTimeMillis()}.wav"
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val outputFile = File(exportDir, outputFileName)

            if (narrationFile != null && narrationFile.exists()) {
                // If narration exists, mix it with synthesized background music
                val narrationBytes = narrationFile.readBytes()
                val (sampleRate, numChannels, pcmData) = extractPcmFromWav(narrationBytes)

                val mixedPcm = ByteArray(pcmData.size)
                val durationSec = (pcmData.size / (sampleRate * numChannels * 2.0))
                val numSamples = pcmData.size / 2

                val baseFreqs = ambience.baseFrequencies

                for (i in 0 until numSamples) {
                    val low = pcmData[i * 2].toInt() and 0xFF
                    val high = pcmData[i * 2 + 1].toInt()
                    val voiceSample = ((high shl 8) or low).toShort()

                    val t = i.toDouble() / sampleRate
                    var musicSampleVal = 0.0
                    for (f in baseFreqs) {
                        musicSampleVal += sin(2 * PI * f * t) * 0.25
                    }

                    val effectiveMusicVol = if (autoDucking) musicVolume * 0.40f else musicVolume
                    val musicSample = (musicSampleVal * effectiveMusicVol * 32767.0).toInt()

                    val finalSample = ((voiceSample * voiceVolume) + musicSample)
                        .coerceIn(-32768f, 32767f).toInt().toShort()

                    mixedPcm[i * 2] = (finalSample.toInt() and 0xFF).toByte()
                    mixedPcm[i * 2 + 1] = ((finalSample.toInt() shr 8) and 0xFF).toByte()
                }

                val wavBytes = createStandardWav(mixedPcm, sampleRate, numChannels)
                FileOutputStream(outputFile).use { it.write(wavBytes) }
            } else {
                // Synthesize 15 seconds of standalone atmospheric soundscape
                val sampleRate = 44100
                val durationSec = 15.0
                val totalSamples = (sampleRate * durationSec).toInt()
                val pcmData = ByteArray(totalSamples * 2)

                for (i in 0 until totalSamples) {
                    val t = i.toDouble() / sampleRate
                    var sampleVal = 0.0
                    for (f in ambience.baseFrequencies) {
                        sampleVal += sin(2 * PI * f * t) * 0.3
                    }
                    val sampleShort = (sampleVal * musicVolume * 32767.0).toInt().coerceIn(-32768, 32767).toShort()
                    pcmData[i * 2] = (sampleShort.toInt() and 0xFF).toByte()
                    pcmData[i * 2 + 1] = ((sampleShort.toInt() shr 8) and 0xFF).toByte()
                }

                val wavBytes = createStandardWav(pcmData, sampleRate, 1)
                FileOutputStream(outputFile).use { it.write(wavBytes) }
            }

            Result.success(outputFile)
        } catch (e: Exception) {
            Log.e("AudioEngine", "Export audio failed", e)
            Result.failure(e)
        }
    }

    private fun extractPcmFromWav(wavBytes: ByteArray): Triple<Int, Int, ByteArray> {
        if (wavBytes.size < 44) return Triple(24000, 1, wavBytes)
        val channels = (wavBytes[22].toInt() and 0xFF) or ((wavBytes[23].toInt() and 0xFF) shl 8)
        val sampleRate = (wavBytes[24].toInt() and 0xFF) or
                ((wavBytes[25].toInt() and 0xFF) shl 8) or
                ((wavBytes[26].toInt() and 0xFF) shl 16) or
                ((wavBytes[27].toInt() and 0xFF) shl 24)

        val pcmData = ByteArray(wavBytes.size - 44)
        System.arraycopy(wavBytes, 44, pcmData, 0, pcmData.size)
        return Triple(sampleRate, channels, pcmData)
    }

    private fun createStandardWav(pcmData: ByteArray, sampleRate: Int, channels: Int): ByteArray {
        val header = ByteArray(44)
        val totalDataLen = pcmData.size + 36
        val byteRate = sampleRate * channels * 2

        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0 // PCM
        header[22] = channels.toByte(); header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * 2).toByte(); header[33] = 0
        header[34] = 16; header[35] = 0 // Bits per sample
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (pcmData.size and 0xff).toByte()
        header[41] = ((pcmData.size shr 8) and 0xff).toByte()
        header[42] = ((pcmData.size shr 16) and 0xff).toByte()
        header[43] = ((pcmData.size shr 24) and 0xff).toByte()

        val fullWav = ByteArray(44 + pcmData.size)
        System.arraycopy(header, 0, fullWav, 0, 44)
        System.arraycopy(pcmData, 0, fullWav, 44, pcmData.size)
        return fullWav
    }

    fun release() {
        stopAll()
        tts?.shutdown()
        tts = null
        scope.cancel()
    }
}
