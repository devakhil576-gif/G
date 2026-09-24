package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiPoetryService
import com.example.audio.AudioEngine
import com.example.audio.PlaybackState
import com.example.data.local.PoetryDatabase
import com.example.data.local.PoetryProjectEntity
import com.example.data.model.*
import com.example.data.repository.ClassicPoem
import com.example.data.repository.PoetryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

enum class StudioTab(val title: String, val hindiUrdu: String) {
    STUDIO("Create", "तख़्लीक़"),
    VOICES("Voices", "सदा-ए-शायर"),
    EDITOR("Line Editor", "मिसरा-ब-मिसरा"),
    EMOTION("Emotion", "जज़्बात"),
    AMBIENCE("Music", "राग-ओ-साज़"),
    MIXER("Mixer", "आवाज़ तराश"),
    PREVIEW("Performance", "मुशायरा"),
    EXPORT("Export", "निकासी"),
    PROJECTS("Projects", "मेरी शायरी")
}

data class StudioUiState(
    val currentProjectId: Long? = null,
    val title: String = "दिल-ए-नादाँ (Dil-e-Nadaan)",
    val poet: String = "Mirza Ghalib (मिर्ज़ा ग़ालिब)",
    val poetryText: String = """
दिल-ए-नादाँ तुझे हुआ क्या है
आख़िर इस दर्द की दवा क्या है

हम हैं मुश्ताक़ और वो बे-ज़ार
या इलाही ये माजरा क्या है

मैं भी मुँह में ज़बान रखता हूँ
काश पूछो कि मुद्दआ क्या है

हम को उन से वफ़ा की है उम्मीद
जो नहीं जानते वफ़ा क्या है
    """.trimIndent(),
    val scriptType: String = "Devanagari / Hindi-Urdu",
    val selectedVoice: VoiceProfile = VoiceCatalog.maleVoices.first(),
    val selectedEmotion: EmotionProfile = EmotionCatalog.emotions.find { it.id == "heartbreak" } ?: EmotionCatalog.emotions.first(),
    val emotionIntensity: Float = 1.35f,
    val speakingSpeed: Float = 0.88f,
    val pitch: Float = 0.95f,
    val voiceDepth: Float = 1.15f,
    val pauseDurationSec: Float = 1.5f,
    val isAiDirected: Boolean = true,
    val warmth: Float = 1.1f,
    val energy: Float = 0.9f,
    val clarity: Float = 1.0f,
    val breathiness: Float = 0.35f,
    val lineConfigs: List<LineNarrationConfig> = emptyList(),
    val analysisResult: PoetryAnalysisResult? = null,
    val isAnalyzing: Boolean = false,
    val isGeneratingNarration: Boolean = false,
    val statusMessage: String? = null,
    val narrationAudioFile: File? = null,
    val selectedAmbience: BackgroundAmbience = AmbienceCatalog.ambiences.find { it.id == "melancholy_cello" } ?: AmbienceCatalog.ambiences.first(),
    val voiceVolume: Float = 0.95f,
    val musicVolume: Float = 0.38f,
    val ambientVolume: Float = 0.30f,
    val reverbLevel: Float = 0.35f,
    val autoDucking: Boolean = true,
    val activeTab: StudioTab = StudioTab.STUDIO,
    val videoAspectRatio: String = "9:16",
    val visualTheme: String = "Velvet Midnight",
    val fontSizeSp: Int = 20,
    val textAlignment: String = "Center",
    val exportedFile: File? = null,
    val comparisonVoices: List<VoiceProfile> = listOf(
        VoiceCatalog.maleVoices[0],
        VoiceCatalog.maleVoices[1],
        VoiceCatalog.maleVoices[2],
        VoiceCatalog.maleVoices[3]
    )
)

class PoetryStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PoetryDatabase.getInstance(application)
    private val repository = PoetryRepository(db.poetryDao())
    private val geminiService = GeminiPoetryService(application)
    val audioEngine = AudioEngine(application)

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = audioEngine.playbackState
    val savedProjects: StateFlow<List<PoetryProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val classicLibrary = PoetryRepository.classicLibrary

    init {
        viewModelScope.launch {
            repository.seedSampleProjectsIfEmpty(0)
            updateLineConfigsFromText(_uiState.value.poetryText)
            audioEngine.setBackgroundAmbience(_uiState.value.selectedAmbience)
            audioEngine.voiceVolume = _uiState.value.voiceVolume
            audioEngine.musicVolume = _uiState.value.musicVolume
            audioEngine.ambientVolume = _uiState.value.ambientVolume
            audioEngine.reverbAmount = _uiState.value.reverbLevel
            audioEngine.autoDucking = _uiState.value.autoDucking
        }
    }

    fun setActiveTab(tab: StudioTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun updatePoetryText(newText: String) {
        _uiState.update { it.copy(poetryText = newText) }
        updateLineConfigsFromText(newText)
    }

    fun updateTitle(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun updatePoet(newPoet: String) {
        _uiState.update { it.copy(poet = newPoet) }
    }

    fun selectVoice(voice: VoiceProfile) {
        _uiState.update {
            it.copy(
                selectedVoice = voice,
                pitch = voice.pitchMultiplier,
                speakingSpeed = voice.speedMultiplier
            )
        }
    }

    fun selectEmotion(emotion: EmotionProfile) {
        _uiState.update {
            it.copy(
                selectedEmotion = emotion,
                speakingSpeed = emotion.recommendedSpeed,
                pitch = emotion.recommendedPitch,
                pauseDurationSec = emotion.pauseIntensity * 1.2f
            )
        }
    }

    fun selectAmbience(ambience: BackgroundAmbience) {
        _uiState.update { it.copy(selectedAmbience = ambience) }
        audioEngine.setBackgroundAmbience(ambience)
    }

    fun updateSliders(
        intensity: Float = _uiState.value.emotionIntensity,
        speed: Float = _uiState.value.speakingSpeed,
        pitch: Float = _uiState.value.pitch,
        depth: Float = _uiState.value.voiceDepth,
        pause: Float = _uiState.value.pauseDurationSec,
        warmth: Float = _uiState.value.warmth,
        reverb: Float = _uiState.value.reverbLevel
    ) {
        _uiState.update {
            it.copy(
                emotionIntensity = intensity,
                speakingSpeed = speed,
                pitch = pitch,
                voiceDepth = depth,
                pauseDurationSec = pause,
                warmth = warmth,
                reverbLevel = reverb
            )
        }
        audioEngine.reverbAmount = reverb
    }

    fun updateMixer(
        voiceVol: Float = _uiState.value.voiceVolume,
        musicVol: Float = _uiState.value.musicVolume,
        ambientVol: Float = _uiState.value.ambientVolume,
        ducking: Boolean = _uiState.value.autoDucking
    ) {
        _uiState.update {
            it.copy(
                voiceVolume = voiceVol,
                musicVolume = musicVol,
                ambientVolume = ambientVol,
                autoDucking = ducking
            )
        }
        audioEngine.voiceVolume = voiceVol
        audioEngine.musicVolume = musicVol
        audioEngine.ambientVolume = ambientVol
        audioEngine.autoDucking = ducking
    }

    fun autoMix() {
        updateMixer(
            voiceVol = 0.95f,
            musicVol = 0.38f,
            ambientVol = 0.28f,
            ducking = true
        )
        _uiState.update { it.copy(statusMessage = "Auto Mix Applied: Balanced Narration & Music") }
    }

    fun applyPreset(preset: PerformancePreset) {
        val voice = VoiceCatalog.getVoiceById(preset.voiceId)
        val emotion = EmotionCatalog.getEmotionById(preset.emotionId)
        val ambience = AmbienceCatalog.getAmbienceById(preset.backgroundMusicId)

        _uiState.update {
            it.copy(
                selectedVoice = voice,
                selectedEmotion = emotion,
                selectedAmbience = ambience,
                emotionIntensity = preset.emotionIntensity,
                speakingSpeed = preset.speakingSpeed,
                voiceDepth = preset.voiceDepth,
                pauseDurationSec = preset.pauseDurationSec,
                reverbLevel = preset.reverbAmount,
                musicVolume = preset.musicVolume,
                voiceVolume = preset.voiceVolume,
                statusMessage = "Preset '${preset.name}' loaded"
            )
        }
        audioEngine.setBackgroundAmbience(ambience)
        audioEngine.voiceVolume = preset.voiceVolume
        audioEngine.musicVolume = preset.musicVolume
        audioEngine.reverbAmount = preset.reverbAmount
    }

    fun applyClassicPoem(poem: ClassicPoem) {
        val voice = VoiceCatalog.getVoiceById(poem.suggestedVoiceId)
        val emotion = EmotionCatalog.getEmotionById(poem.suggestedEmotionId)
        val ambience = AmbienceCatalog.getAmbienceById(poem.suggestedAmbienceId)

        _uiState.update {
            it.copy(
                title = poem.title,
                poet = poem.poet,
                poetryText = poem.content,
                selectedVoice = voice,
                selectedEmotion = emotion,
                selectedAmbience = ambience,
                statusMessage = "Loaded '${poem.title}'"
            )
        }
        updateLineConfigsFromText(poem.content)
        audioEngine.setBackgroundAmbience(ambience)
    }

    fun analyzePoetryWithGemini() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, statusMessage = "AI analyzing poem mood and rhythm...") }
            val result = geminiService.analyzePoetry(_uiState.value.poetryText)
            result.onSuccess { analysis ->
                val recEmotion = EmotionCatalog.getEmotionById(analysis.detectedEmotionId)
                val recVoice = VoiceCatalog.getVoiceById(analysis.recommendedVoiceId)
                val recAmbience = AmbienceCatalog.getAmbienceById(analysis.recommendedAmbienceId)

                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        analysisResult = analysis,
                        selectedEmotion = recEmotion,
                        selectedVoice = recVoice,
                        selectedAmbience = recAmbience,
                        emotionIntensity = analysis.emotionalIntensity,
                        statusMessage = "AI analysis complete: ${analysis.detectedMood}"
                    )
                }
                audioEngine.setBackgroundAmbience(recAmbience)
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAnalyzing = false,
                        statusMessage = "Analysis completed with local guidance: ${err.message}"
                    )
                }
            }
        }
    }

    fun generateFullPerformanceNarration() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGeneratingNarration = true,
                    statusMessage = "Generating male voice performance using gemini-3.8-flash-tts..."
                )
            }

            val state = _uiState.value
            val emotionDirective = "${state.selectedEmotion.geminiDirective} Emotion intensity: ${state.emotionIntensity}. Pause between lines: ${state.pauseDurationSec} seconds. Voice depth: ${state.voiceDepth}."

            val result = geminiService.generateExpressiveNarration(
                text = state.poetryText,
                voiceName = state.selectedVoice.geminiVoiceName,
                emotionDirective = emotionDirective,
                speedMultiplier = state.speakingSpeed,
                voiceDepth = state.voiceDepth
            )

            result.onSuccess { audioFile ->
                val lines = state.poetryText.lines().filter { it.isNotBlank() }
                audioEngine.setNarrationFile(audioFile, lines.size)

                _uiState.update {
                    it.copy(
                        isGeneratingNarration = false,
                        narrationAudioFile = audioFile,
                        activeTab = StudioTab.PREVIEW,
                        statusMessage = "Narration generated successfully! Ready to play."
                    )
                }
                // Auto play performance in preview
                audioEngine.playPerformance()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isGeneratingNarration = false,
                        statusMessage = "Gemini voice generation note: ${error.message}. You can also use instant offline preview."
                    )
                }
            }
        }
    }

    fun previewVoice(voice: VoiceProfile) {
        audioEngine.previewVoiceOffline(
            voice.sampleText,
            pitch = voice.pitchMultiplier,
            speed = voice.speedMultiplier
        )
    }

    fun playPausePerformance() {
        if (playbackState.value.isPlaying) {
            audioEngine.pause()
        } else {
            val file = _uiState.value.narrationAudioFile
            if (file != null && file.exists()) {
                audioEngine.resume()
            } else {
                // If audio not yet synthesized via API, speak text with Android TTS and play ambience
                audioEngine.previewVoiceOffline(
                    _uiState.value.poetryText,
                    pitch = _uiState.value.pitch,
                    speed = _uiState.value.speakingSpeed
                )
                audioEngine.playPerformance()
            }
        }
    }

    fun seekPlayback(positionMs: Long) {
        audioEngine.seekTo(positionMs)
    }

    fun stopPlayback() {
        audioEngine.stopAll()
    }

    fun exportFinalAudio() {
        viewModelScope.launch {
            _uiState.update { it.copy(statusMessage = "Mixing and exporting master audio...") }
            val result = audioEngine.exportMasterAudio(
                narrationFile = _uiState.value.narrationAudioFile,
                ambience = _uiState.value.selectedAmbience
            )
            result.onSuccess { exported ->
                _uiState.update {
                    it.copy(
                        exportedFile = exported,
                        statusMessage = "Master audio exported: ${exported.name}"
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(statusMessage = "Export failed: ${err.message}") }
            }
        }
    }

    fun saveProject() {
        viewModelScope.launch {
            val s = _uiState.value
            val entity = PoetryProjectEntity(
                id = s.currentProjectId ?: 0L,
                title = s.title.ifBlank { "Untitled Ghazal" },
                poet = s.poet.ifBlank { "Poet" },
                content = s.poetryText,
                scriptType = s.scriptType,
                selectedVoiceId = s.selectedVoice.id,
                selectedEmotionId = s.selectedEmotion.id,
                emotionIntensity = s.emotionIntensity,
                speakingSpeed = s.speakingSpeed,
                pitch = s.pitch,
                voiceDepth = s.voiceDepth,
                pauseDurationSec = s.pauseDurationSec,
                backgroundMusicId = s.selectedAmbience.id,
                musicVolume = s.musicVolume,
                voiceVolume = s.voiceVolume,
                ambientVolume = s.ambientVolume,
                reverbAmount = s.reverbLevel,
                autoDucking = s.autoDucking,
                isAiDirected = s.isAiDirected,
                videoAspectRatio = s.videoAspectRatio,
                visualTheme = s.visualTheme,
                audioFilePath = s.narrationAudioFile?.absolutePath,
                updatedAt = System.currentTimeMillis()
            )
            val newId = repository.saveProject(entity)
            _uiState.update {
                it.copy(
                    currentProjectId = if (s.currentProjectId != null && s.currentProjectId > 0) s.currentProjectId else newId,
                    statusMessage = "Project '${entity.title}' saved to library"
                )
            }
        }
    }

    fun loadProject(project: PoetryProjectEntity) {
        val voice = VoiceCatalog.getVoiceById(project.selectedVoiceId)
        val emotion = EmotionCatalog.getEmotionById(project.selectedEmotionId)
        val ambience = AmbienceCatalog.getAmbienceById(project.backgroundMusicId)
        val audioFile = project.audioFilePath?.let { File(it) }?.takeIf { it.exists() }

        _uiState.update {
            it.copy(
                currentProjectId = project.id,
                title = project.title,
                poet = project.poet,
                poetryText = project.content,
                scriptType = project.scriptType,
                selectedVoice = voice,
                selectedEmotion = emotion,
                selectedAmbience = ambience,
                emotionIntensity = project.emotionIntensity,
                speakingSpeed = project.speakingSpeed,
                pitch = project.pitch,
                voiceDepth = project.voiceDepth,
                pauseDurationSec = project.pauseDurationSec,
                musicVolume = project.musicVolume,
                voiceVolume = project.voiceVolume,
                ambientVolume = project.ambientVolume,
                reverbLevel = project.reverbAmount,
                autoDucking = project.autoDucking,
                isAiDirected = project.isAiDirected,
                videoAspectRatio = project.videoAspectRatio,
                visualTheme = project.visualTheme,
                narrationAudioFile = audioFile,
                activeTab = StudioTab.STUDIO,
                statusMessage = "Loaded project '${project.title}'"
            )
        }
        updateLineConfigsFromText(project.content)
        audioEngine.setBackgroundAmbience(ambience)
        audioFile?.let {
            val lines = project.content.lines().filter { l -> l.isNotBlank() }
            audioEngine.setNarrationFile(it, lines.size)
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_uiState.value.currentProjectId == id) {
                _uiState.update { it.copy(currentProjectId = null) }
            }
        }
    }

    fun setAspectRatio(ratio: String) {
        _uiState.update { it.copy(videoAspectRatio = ratio) }
    }

    fun setVisualTheme(theme: String) {
        _uiState.update { it.copy(visualTheme = theme) }
    }

    fun setScriptType(script: String) {
        _uiState.update { it.copy(scriptType = script) }
    }

    fun updateLineConfig(index: Int, config: LineNarrationConfig) {
        val updated = _uiState.value.lineConfigs.toMutableList()
        if (index in updated.indices) {
            updated[index] = config
            _uiState.update { it.copy(lineConfigs = updated) }
        }
    }

    fun clearStatusMessage() {
        _uiState.update { it.copy(statusMessage = null) }
    }

    private fun updateLineConfigsFromText(text: String) {
        val rawLines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val configs = rawLines.mapIndexed { idx, line ->
            LineNarrationConfig(
                lineIndex = idx,
                text = line,
                emotionId = _uiState.value.selectedEmotion.id,
                speedMultiplier = _uiState.value.speakingSpeed,
                pauseBeforeMs = if (idx == 0) 500L else 300L,
                pauseAfterMs = if (idx % 2 == 1) 1800L else 1200L, // Couplet ending pause
                deliveryStyle = "Natural Poetic",
                estimatedDurationMs = (line.length * 95L).coerceIn(2500L, 6500L)
            )
        }
        _uiState.update { it.copy(lineConfigs = configs) }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
