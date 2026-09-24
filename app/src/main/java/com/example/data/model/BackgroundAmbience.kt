package com.example.data.model

data class BackgroundAmbience(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val baseFrequencies: List<Float>, // For musical chord/drone synthesis
    val waveformType: WaveformType,
    val tempoBpm: Int,
    val defaultVolume: Float = 0.40f,
    val recommendedEmotions: List<String>
)

enum class WaveformType {
    WARM_PIANO_PAD,
    MELLOW_CELLO,
    SUFI_TANPURA_DRONE,
    RAIN_AND_ATMOSPHERE,
    CINEMATIC_SUB,
    ETHEREAL_STRINGS,
    MINIMAL_PIANO
}

object AmbienceCatalog {
    val ambiences = listOf(
        BackgroundAmbience(
            id = "romantic_piano",
            name = "Warm Midnight Piano",
            category = "Romantic",
            description = "Soft, gentle acoustic chords in major 7ths, tender and intimate.",
            baseFrequencies = listOf(261.63f, 329.63f, 392.00f, 493.88f), // Cmaj7
            waveformType = WaveformType.WARM_PIANO_PAD,
            tempoBpm = 60,
            defaultVolume = 0.38f,
            recommendedEmotions = listOf("love", "romance", "peace", "happiness")
        ),
        BackgroundAmbience(
            id = "melancholy_cello",
            name = "Melancholy Cello & Rain",
            category = "Sad / Heartbreak",
            description = "Slow minor tonalities echoing sorrow, tearful longing and loss.",
            baseFrequencies = listOf(220.00f, 261.63f, 329.63f, 392.00f), // Am7
            waveformType = WaveformType.MELLOW_CELLO,
            tempoBpm = 50,
            defaultVolume = 0.35f,
            recommendedEmotions = listOf("sadness", "heartbreak", "pain", "loneliness")
        ),
        BackgroundAmbience(
            id = "sufi_tanpura",
            name = "Sufi Tanpura & Ney Drone",
            category = "Classical Ghazal",
            description = "Traditional mystical drone evoking sacred mehfils and divine poetry.",
            baseFrequencies = listOf(146.83f, 220.00f, 293.66f, 440.00f), // D-A-D Sa-Pa
            waveformType = WaveformType.SUFI_TANPURA_DRONE,
            tempoBpm = 45,
            defaultVolume = 0.40f,
            recommendedEmotions = listOf("devotion", "longing", "reflection")
        ),
        BackgroundAmbience(
            id = "late_night_rain",
            name = "Late-Night Window Rain",
            category = "Nostalgic / Solitude",
            description = "Gentle rain patter on glass with slow nostalgic electric piano.",
            baseFrequencies = listOf(174.61f, 220.00f, 261.63f, 329.63f), // Fmaj7
            waveformType = WaveformType.RAIN_AND_ATMOSPHERE,
            tempoBpm = 52,
            defaultVolume = 0.42f,
            recommendedEmotions = listOf("nostalgia", "loneliness", "reflection")
        ),
        BackgroundAmbience(
            id = "cinematic_atmosphere",
            name = "Deep Cinematic Atmosphere",
            category = "Dramatic / Intense",
            description = "Low sub-bass drone and theatrical orchestral pads for deep drama.",
            baseFrequencies = listOf(110.00f, 164.81f, 220.00f, 329.63f), // Low A minor
            waveformType = WaveformType.CINEMATIC_SUB,
            tempoBpm = 55,
            defaultVolume = 0.45f,
            recommendedEmotions = listOf("intensity", "anger", "mystery")
        ),
        BackgroundAmbience(
            id = "ethereal_ambient",
            name = "Ethereal Celestial Strings",
            category = "Dreamy / Peaceful",
            description = "Luminous, floating ambient soundscape that cradles the poetry.",
            baseFrequencies = listOf(293.66f, 369.99f, 440.00f, 587.33f), // Dadd9
            waveformType = WaveformType.ETHEREAL_STRINGS,
            tempoBpm = 48,
            defaultVolume = 0.36f,
            recommendedEmotions = listOf("peace", "hope", "love", "devotion")
        ),
        BackgroundAmbience(
            id = "classical_sarod",
            name = "Classical Sarod & Pad",
            category = "Classical",
            description = "Resonant acoustic raga intervals suited for Ghalib, Mir, and Faiz.",
            baseFrequencies = listOf(196.00f, 246.94f, 293.66f, 392.00f), // G Major
            waveformType = WaveformType.SUFI_TANPURA_DRONE,
            tempoBpm = 58,
            defaultVolume = 0.38f,
            recommendedEmotions = listOf("reflection", "longing", "devotion")
        ),
        BackgroundAmbience(
            id = "minimal_piano",
            name = "Minimalist Solo Piano",
            category = "Minimal",
            description = "Sparse, crystal-clear piano drops with deep natural reverb.",
            baseFrequencies = listOf(261.63f, 392.00f, 523.25f, 659.25f),
            waveformType = WaveformType.MINIMAL_PIANO,
            tempoBpm = 54,
            defaultVolume = 0.35f,
            recommendedEmotions = listOf("sadness", "reflection", "peace")
        ),
        BackgroundAmbience(
            id = "dark_ambient",
            name = "Shadows & Distant Thunder",
            category = "Dark / Mystery",
            description = "Eerie, low-frequency suspense drone and reverberant echo.",
            baseFrequencies = listOf(98.00f, 146.83f, 196.00f, 293.66f),
            waveformType = WaveformType.CINEMATIC_SUB,
            tempoBpm = 40,
            defaultVolume = 0.40f,
            recommendedEmotions = listOf("mystery", "intensity", "anger")
        )
    )

    fun getAmbienceById(id: String): BackgroundAmbience {
        return ambiences.find { it.id == id } ?: ambiences.first()
    }

    fun recommendForEmotion(emotionId: String): BackgroundAmbience {
        return ambiences.firstOrNull { it.recommendedEmotions.contains(emotionId) }
            ?: ambiences.first()
    }
}
