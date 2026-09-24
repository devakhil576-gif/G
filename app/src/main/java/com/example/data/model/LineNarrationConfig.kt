package com.example.data.model

data class LineNarrationConfig(
    val lineIndex: Int,
    val text: String,
    val emotionId: String = "love",
    val voiceIntensity: Float = 1.0f,
    val speedMultiplier: Float = 1.0f,
    val pauseBeforeMs: Long = 300L,
    val pauseAfterMs: Long = 1200L,
    val deliveryStyle: String = "Natural Poetic", // "Whisper", "Resonant", "Lyrical", "Intense", "Natural Poetic"
    val emphasis: String = "",
    val estimatedDurationMs: Long = 3500L,
    val startTimeMs: Long = 0L
)

data class PoetryAnalysisResult(
    val detectedMood: String,
    val detectedEmotionId: String,
    val emotionalIntensity: Float, // 0.0 to 2.0
    val rhythmicStructure: String,
    val meaningSummary: String,
    val pronunciationTips: List<PronunciationTip>,
    val recommendedVoiceId: String,
    val recommendedAmbienceId: String,
    val dramaticLineIndices: List<Int>
)

data class PronunciationTip(
    val word: String,
    val phoneticUrduHindi: String,
    val note: String
)
