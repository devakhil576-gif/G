package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poetry_projects")
data class PoetryProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val poet: String,
    val content: String,
    val scriptType: String = "Hindi/Urdu", // "Devanagari", "Urdu", "Roman"
    val selectedVoiceId: String = "deep_emotional",
    val selectedEmotionId: String = "heartbreak",
    val emotionIntensity: Float = 1.2f,
    val speakingSpeed: Float = 0.90f,
    val pitch: Float = 1.0f,
    val voiceDepth: Float = 1.1f,
    val pauseDurationSec: Float = 1.4f,
    val backgroundMusicId: String = "melancholy_cello",
    val musicVolume: Float = 0.35f,
    val voiceVolume: Float = 0.95f,
    val ambientVolume: Float = 0.30f,
    val reverbAmount: Float = 0.35f,
    val autoDucking: Boolean = true,
    val isAiDirected: Boolean = true,
    val videoAspectRatio: String = "9:16", // "9:16", "16:9", "1:1"
    val visualTheme: String = "Velvet Midnight", // "Velvet Midnight", "Royal Mughal Gold", "Obsidian Stars", "Ruby Twilight"
    val audioFilePath: String? = null,
    val lineConfigsJson: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
