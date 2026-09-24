package com.example.data.model

import androidx.compose.ui.graphics.Color

data class EmotionProfile(
    val id: String,
    val name: String,
    val hindiUrduName: String,
    val description: String,
    val accentColor: Color,
    val recommendedSpeed: Float = 0.95f,
    val recommendedPitch: Float = 1.0f,
    val pauseIntensity: Float = 1.2f, // Multiplier for line pauses
    val voiceDepth: Float = 1.1f,
    val breathiness: Float = 0.3f,
    val geminiDirective: String
)

object EmotionCatalog {
    val emotions = listOf(
        EmotionProfile(
            id = "love",
            name = "Love",
            hindiUrduName = "इश्क़ / मोहब्बत",
            description = "Tender, devoted affection, warm and glowing delivery.",
            accentColor = Color(0xFFE25A77),
            recommendedSpeed = 0.92f,
            recommendedPitch = 1.02f,
            pauseIntensity = 1.1f,
            geminiDirective = "Perform with deep tender love and adoration, gentle melodic warmth, soft emphasis on endearing words."
        ),
        EmotionProfile(
            id = "romance",
            name = "Romance",
            hindiUrduName = "रोमांस / इश्क़िया",
            description = "Sultry, breathless, delicate and musical cadence.",
            accentColor = Color(0xFFFF7597),
            recommendedSpeed = 0.90f,
            recommendedPitch = 0.98f,
            pauseIntensity = 1.3f,
            geminiDirective = "Deliver in a soft romantic whisper-tone, intimate phrasing, slow lingering cadence on evocative metaphors."
        ),
        EmotionProfile(
            id = "sadness",
            name = "Sadness",
            hindiUrduName = "उदासी / ग़म",
            description = "Quiet melancholy, gentle sorrow, muted resonance.",
            accentColor = Color(0xFF7E8EA5),
            recommendedSpeed = 0.85f,
            recommendedPitch = 0.94f,
            pauseIntensity = 1.4f,
            geminiDirective = "Narrate with solemn sorrow, subdued melancholy, vulnerable soft cadence and downcast sentence endings."
        ),
        EmotionProfile(
            id = "heartbreak",
            name = "Heartbreak",
            hindiUrduName = "शिकस्त / दर्द-ए-दिल",
            description = "Raw emotional ache, fragile hesitation, wounded depth.",
            accentColor = Color(0xFFD32F2F),
            recommendedSpeed = 0.82f,
            recommendedPitch = 0.90f,
            pauseIntensity = 1.6f,
            geminiDirective = "Perform with trembling emotional ache, heavy sighs, poignant pauses before heart-wrenching words, deep grief."
        ),
        EmotionProfile(
            id = "longing",
            name = "Longing",
            hindiUrduName = "हिज्र / तलब / जुस्तजू",
            description = "Yearning for the beloved, distance, restless craving.",
            accentColor = Color(0xFFBA68C8),
            recommendedSpeed = 0.88f,
            recommendedPitch = 0.96f,
            pauseIntensity = 1.35f,
            geminiDirective = "Express deep yearning and longing across time and distance, prolonged vowel sounds, wistful questioning tone."
        ),
        EmotionProfile(
            id = "nostalgia",
            name = "Nostalgia",
            hindiUrduName = "याद-ए-माज़ी / बिछड़े दिन",
            description = "Gentle reminiscence, bittersweet memories of the past.",
            accentColor = Color(0xFFC49760),
            recommendedSpeed = 0.90f,
            recommendedPitch = 0.97f,
            pauseIntensity = 1.25f,
            geminiDirective = "Read with warm bittersweet nostalgia, a smile tinged with wistful reflection on memories long departed."
        ),
        EmotionProfile(
            id = "hope",
            name = "Hope",
            hindiUrduName = "उम्मीद / सहर",
            description = "Awakening dawn, resilient light emerging from darkness.",
            accentColor = Color(0xFFF9A825),
            recommendedSpeed = 1.0f,
            recommendedPitch = 1.05f,
            pauseIntensity = 1.0f,
            geminiDirective = "Deliver with rising optimism and inspiring confidence, clear radiant tone, uplifting cadence."
        ),
        EmotionProfile(
            id = "anger",
            name = "Anger",
            hindiUrduName = "ग़ुस्सा / जलाल",
            description = "Fierce defiance, burning indignation, sharp consonants.",
            accentColor = Color(0xFFE64A19),
            recommendedSpeed = 1.05f,
            recommendedPitch = 0.92f,
            pauseIntensity = 1.2f,
            geminiDirective = "Deliver with controlled fury and fierce indignation, crisp hard consonants, forceful resonant emphasis."
        ),
        EmotionProfile(
            id = "pain",
            name = "Pain",
            hindiUrduName = "दर्द / कर्ब",
            description = "Internal suffering, suffocating agony, weary heaviness.",
            accentColor = Color(0xFF8E24AA),
            recommendedSpeed = 0.80f,
            recommendedPitch = 0.88f,
            pauseIntensity = 1.5f,
            geminiDirective = "Deliver with agonizing gravity, tired raspy undertones, heavy vocal weight and weary breathing."
        ),
        EmotionProfile(
            id = "loneliness",
            name = "Loneliness",
            hindiUrduName = "तन्हाई / ख़ामोशी",
            description = "Echoing stillness, desolate night, solitude.",
            accentColor = Color(0xFF546E7A),
            recommendedSpeed = 0.83f,
            recommendedPitch = 0.92f,
            pauseIntensity = 1.5f,
            geminiDirective = "Perform as an isolated soliloquy in an empty room, hollow reverberant stillness, lingering quiet endings."
        ),
        EmotionProfile(
            id = "happiness",
            name = "Happiness",
            hindiUrduName = "मसर्रत / शादमानी",
            description = "Joyous bloom, sparkling wit, celebratory delight.",
            accentColor = Color(0xFF43A047),
            recommendedSpeed = 1.04f,
            recommendedPitch = 1.06f,
            pauseIntensity = 0.9f,
            geminiDirective = "Recite with glowing joy, crisp musical bounce, smiling tone, cheerful celebration of life."
        ),
        EmotionProfile(
            id = "devotion",
            name = "Devotion",
            hindiUrduName = "अक़ीदत / सूफ़ियाना",
            description = "Sacred reverence, mystic surrender, transcendental peace.",
            accentColor = Color(0xFF00897B),
            recommendedSpeed = 0.86f,
            recommendedPitch = 0.94f,
            pauseIntensity = 1.4f,
            geminiDirective = "Perform with profound mystical devotion, meditative humility, spiritual depth, echoing sacred chants."
        ),
        EmotionProfile(
            id = "mystery",
            name = "Mystery",
            hindiUrduName = "राज़ / पुर-इसरार",
            description = "Enigmatic shadows, whispered secrets, lingering suspense.",
            accentColor = Color(0xFF5E35B1),
            recommendedSpeed = 0.87f,
            recommendedPitch = 0.91f,
            pauseIntensity = 1.45f,
            geminiDirective = "Convey dark intrigue and enigmatic suspense, hushed conspiratorial tone, lingering question inflections."
        ),
        EmotionProfile(
            id = "intensity",
            name = "Intensity",
            hindiUrduName = "शिद्दत / जोश",
            description = "Electrifying passion, overwhelming emotional momentum.",
            accentColor = Color(0xFFD81B60),
            recommendedSpeed = 1.02f,
            recommendedPitch = 0.97f,
            pauseIntensity = 1.1f,
            geminiDirective = "Recite with gripping dramatic intensity, escalating urgency, powerful vocal timbre and piercing conviction."
        ),
        EmotionProfile(
            id = "peace",
            name = "Peace",
            hindiUrduName = "सुकून / इत्मीनान",
            description = "Serene tranquility, soothing harmony, calm breathing.",
            accentColor = Color(0xFF26A69A),
            recommendedSpeed = 0.89f,
            recommendedPitch = 0.98f,
            pauseIntensity = 1.2f,
            geminiDirective = "Deliver in serene stillness, calm meditative pacing, velvety smooth voice that relaxes the soul."
        ),
        EmotionProfile(
            id = "reflection",
            name = "Reflection",
            hindiUrduName = "फ़िक्र / तदब्बुर",
            description = "Philosophical contemplation, searching for truth.",
            accentColor = Color(0xFF78909C),
            recommendedSpeed = 0.88f,
            recommendedPitch = 0.95f,
            pauseIntensity = 1.35f,
            geminiDirective = "Recite with deep philosophical contemplation, pondering each philosophical realization with gravity."
        )
    )

    fun getEmotionById(id: String): EmotionProfile {
        return emotions.find { it.id == id } ?: emotions.first()
    }
}
