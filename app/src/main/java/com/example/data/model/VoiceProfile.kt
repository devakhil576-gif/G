package com.example.data.model

data class VoiceProfile(
    val id: String,
    val geminiVoiceName: String, // Prebuilt Gemini voice mapping (Charon, Fenrir, Orus, Puck, Zephyr)
    val name: String,
    val titleHindiUrdu: String,
    val style: String,
    val description: String,
    val pitchMultiplier: Float = 1.0f,
    val speedMultiplier: Float = 1.0f,
    val sampleText: String = "दिल-ए-नादाँ तुझे हुआ क्या है, आख़िर इस दर्द की दवा क्या है...",
    val sampleTextUrdu: String = "دلِ ناداں تجھے ہوا کیا ہے، آخر اس درد کی دوا کیا ہے...",
    val tag: String,
    val isGeminiNative: Boolean = true
)

object VoiceCatalog {
    val maleVoices = listOf(
        VoiceProfile(
            id = "deep_emotional",
            geminiVoiceName = "Charon",
            name = "Charon — Deep Emotional",
            titleHindiUrdu = "शायर-ए-दर्द (शदीद जज़्बात)",
            style = "Deep, Resonant, Tragic Gravitas",
            description = "Heavy emotional weight with long meditative pauses, perfect for heartbreak, grief, and melancholic ghazals.",
            pitchMultiplier = 0.90f,
            speedMultiplier = 0.88f,
            tag = "Signature Ghazal"
        ),
        VoiceProfile(
            id = "soft_romantic",
            geminiVoiceName = "Zephyr",
            name = "Zephyr — Soft Romantic",
            titleHindiUrdu = "आशिक़-ए-सुकून (नर्म मोहब्बत)",
            style = "Intimate, Velvety, Gentle",
            description = "Tender and breathless delivery, ideal for nazms of love, late-night confession, and romantic shayari.",
            pitchMultiplier = 1.02f,
            speedMultiplier = 0.94f,
            tag = "Romantic"
        ),
        VoiceProfile(
            id = "mature_poet",
            geminiVoiceName = "Charon",
            name = "Mirza — Mature Poetic Male",
            titleHindiUrdu = "उस्ताद-ए-सुख़न (दास्तानगोई)",
            style = "Experienced, Articulate, Classical",
            description = "Rich timber of an Urdu master shayar. Flawless Persian-Urdu nuqta pronunciation (ख़, ग़, क़, ज़).",
            pitchMultiplier = 0.92f,
            speedMultiplier = 0.90f,
            tag = "Classical Master"
        ),
        VoiceProfile(
            id = "deep_cinematic",
            geminiVoiceName = "Fenrir",
            name = "Fenrir — Deep Cinematic",
            titleHindiUrdu = "सिनेमैटिक दास्तान (पुर-असर)",
            style = "Sub-Bass, Epic, Powerful",
            description = "Deep cinematic trailer tone, giving philosophical and intense couplets an immersive theatrical resonance.",
            pitchMultiplier = 0.85f,
            speedMultiplier = 0.92f,
            tag = "Cinematic"
        ),
        VoiceProfile(
            id = "calm_storyteller",
            geminiVoiceName = "Orus",
            name = "Orus — Calm Storyteller",
            titleHindiUrdu = "क़िस्सा-गो (मतानत-ओ-संजिदगी)",
            style = "Steady, Warm, Contemplative",
            description = "Measured and soothing cadence suited for long nazms, reflective stories, and Sufi wisdom.",
            pitchMultiplier = 0.96f,
            speedMultiplier = 0.95f,
            tag = "Storyteller"
        ),
        VoiceProfile(
            id = "melancholic_sad",
            geminiVoiceName = "Charon",
            name = "Farhan — Melancholic & Sad",
            titleHindiUrdu = "हसरत-नसीब (उदासी-ओ-फ़िराक़)",
            style = "Hurt, Vulnerable, Tearful",
            description = "Choked vocal nuances, soft trailing line endings, and sighs reflecting deep separation (Hijr).",
            pitchMultiplier = 0.88f,
            speedMultiplier = 0.85f,
            tag = "Sadness"
        ),
        VoiceProfile(
            id = "passionate_poetic",
            geminiVoiceName = "Puck",
            name = "Tariq — Passionate Poetic",
            titleHindiUrdu = "शायर-ए-इंक़िलाब (जोशीला)",
            style = "Dynamic, Rhythmic, Spirited",
            description = "Strong rhythm and emotional crescendo for revolutionary and motivational poetry (Faiz / Iqbal style).",
            pitchMultiplier = 1.05f,
            speedMultiplier = 1.02f,
            tag = "Passionate"
        ),
        VoiceProfile(
            id = "powerful_dramatic",
            geminiVoiceName = "Fenrir",
            name = "Sultan — Powerful Dramatic",
            titleHindiUrdu = "जलाल-ए-अल्फाज़ (शहंशाह)",
            style = "Authoritative, Bold, Resolute",
            description = "Commanding performance with dramatic pauses and intense vocal projection.",
            pitchMultiplier = 0.88f,
            speedMultiplier = 0.96f,
            tag = "Dramatic"
        ),
        VoiceProfile(
            id = "gentle_urdu_shayar",
            geminiVoiceName = "Zephyr",
            name = "Qais — Gentle Urdu Shayar",
            titleHindiUrdu = "नर्म लहजा शायर (तहज़ीब)",
            style = "Lyrical, Cultured, Melodious",
            description = "Gentle Lucknowi and Dehlawi cadence, savoring every radif and qafiya with cultural authenticity.",
            pitchMultiplier = 0.98f,
            speedMultiplier = 0.90f,
            tag = "Tehzeeb"
        ),
        VoiceProfile(
            id = "classical_ghazal",
            geminiVoiceName = "Charon",
            name = "Zohrab — Classical Ghazal",
            titleHindiUrdu = "ग़ज़ल-सराय (सूफ़ियाना)",
            style = "Traditional, Elevated, Soulful",
            description = "Traditional mehfil-style delivery, lingering on poignant phrases and poetic questions.",
            pitchMultiplier = 0.93f,
            speedMultiplier = 0.88f,
            tag = "Mehfil"
        ),
        VoiceProfile(
            id = "natural_conversational",
            geminiVoiceName = "Puck",
            name = "Kabir — Natural Conversational",
            titleHindiUrdu = "गुफ़्तगू-नुमा (सहज अंदाज़)",
            style = "Casual, Relatable, Modern",
            description = "Modern Hindustani spoken style, ideal for contemporary poems and free verse storytelling.",
            pitchMultiplier = 1.0f,
            speedMultiplier = 1.0f,
            tag = "Conversational"
        ),
        VoiceProfile(
            id = "warm_intimate",
            geminiVoiceName = "Orus",
            name = "Aftab — Warm Intimate",
            titleHindiUrdu = "राज़-दाराना (शब-ए-तन्हाई)",
            style = "Low-Key, Close-Mic, Comforting",
            description = "Warm nocturnal intimacy, as if sharing secret verses by candlelight.",
            pitchMultiplier = 0.94f,
            speedMultiplier = 0.92f,
            tag = "Intimate"
        )
    )

    fun getVoiceById(id: String): VoiceProfile {
        return maleVoices.find { it.id == id } ?: maleVoices.first()
    }
}
