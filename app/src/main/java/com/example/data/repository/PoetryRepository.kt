package com.example.data.repository

import com.example.data.local.PoetryDao
import com.example.data.local.PoetryProjectEntity
import kotlinx.coroutines.flow.Flow

data class ClassicPoem(
    val title: String,
    val poet: String,
    val era: String,
    val scriptType: String,
    val content: String,
    val contentUrdu: String,
    val englishMeaning: String,
    val suggestedVoiceId: String,
    val suggestedEmotionId: String,
    val suggestedAmbienceId: String
)

class PoetryRepository(private val dao: PoetryDao) {
    val allProjects: Flow<List<PoetryProjectEntity>> = dao.getAllProjects()
    val favoriteProjects: Flow<List<PoetryProjectEntity>> = dao.getFavoriteProjects()

    fun getProjectById(id: Long): Flow<PoetryProjectEntity?> = dao.getProjectById(id)

    suspend fun saveProject(project: PoetryProjectEntity): Long = dao.insertProject(project)

    suspend fun updateProject(project: PoetryProjectEntity) = dao.updateProject(project)

    suspend fun deleteProject(id: Long) = dao.deleteProjectById(id)

    suspend fun toggleFavorite(id: Long) = dao.toggleFavorite(id)

    suspend fun seedSampleProjectsIfEmpty(existingCount: Int) {
        if (existingCount == 0) {
            val sample = PoetryProjectEntity(
                title = "दिल-ए-नादाँ (Dil-e-Nadaan)",
                poet = "Mirza Ghalib (मिर्ज़ा ग़ालिब)",
                content = """
                    दिल-ए-नादाँ तुझे हुआ क्या है
                    आख़िर इस दर्द की दवा क्या है

                    हम हैं मुश्ताक़ और वो बे-ज़ार
                    या इलाही ये माजरा क्या है

                    मैं भी मुँह में ज़बान रखता हूँ
                    काश पूछो कि मुद्दआ क्या है

                    हम को उन से वफ़ा की है उम्मीद
                    जो नहीं जानते वफ़ा क्या है
                """.trimIndent(),
                scriptType = "Devanagari / Hindi-Urdu",
                selectedVoiceId = "deep_emotional",
                selectedEmotionId = "heartbreak",
                emotionIntensity = 1.35f,
                speakingSpeed = 0.88f,
                pitch = 0.95f,
                voiceDepth = 1.15f,
                pauseDurationSec = 1.5f,
                backgroundMusicId = "melancholy_cello",
                musicVolume = 0.38f,
                voiceVolume = 0.92f,
                visualTheme = "Velvet Midnight",
                videoAspectRatio = "9:16",
                isFavorite = true
            )
            dao.insertProject(sample)
        }
    }

    companion object {
        val classicLibrary = listOf(
            ClassicPoem(
                title = "दिल-ए-नादाँ (Dil-e-Nadaan)",
                poet = "Mirza Asadullah Khan Ghalib",
                era = "Mughal Delhi (1797–1869)",
                scriptType = "Hindi-Urdu",
                content = """
                    दिल-ए-नादाँ तुझे हुआ क्या है
                    आख़िर इस दर्द की दवा क्या है

                    हम हैं मुश्ताक़ और वो बे-ज़ार
                    या इलाही ये माजरा क्या है

                    मैं भी मुँह में ज़बान रखता हूँ
                    काश पूछो कि मुद्दआ क्या है

                    हम को उन से वफ़ा की है उम्मीद
                    जो नहीं जानते वफ़ा क्या है
                """.trimIndent(),
                contentUrdu = """
                    دلِ ناداں تجھے ہوا کیا ہے
                    آخر اس درد کی دوا کیا ہے

                    ہم ہیں مشتاق اور وہ بیزار
                    یا الہی یہ ماجرا کیا ہے

                    میں بھی منہ میں زبان رکھتا ہوں
                    کاش پوچھو کہ مدعا کیا ہے

                    ہم کو ان سے وفا کی ہے امید
                    جو نہیں جانتے وفا کیا ہے
                """.trimIndent(),
                englishMeaning = "O foolish heart, what has befallen thee? What indeed is the cure for this relentless ache?",
                suggestedVoiceId = "deep_emotional",
                suggestedEmotionId = "heartbreak",
                suggestedAmbienceId = "melancholy_cello"
            ),
            ClassicPoem(
                title = "मुझ से पहली सी मोहब्बत (Pehli Si Mohabbat)",
                poet = "Faiz Ahmad Faiz",
                era = "Progressive Writers Movement",
                scriptType = "Hindi-Urdu",
                content = """
                    मुझ से पहली सी मोहब्बत मिरी महबूब न माँग
                    मैंने समझा था कि तू है तो दरख़्शाँ है हयात

                    तेरा ग़म है तो ग़म-ए-दहर का झगड़ा क्या है
                    तेरी सूरत से है आलम में बहारों को सबात

                    और भी दुख हैं ज़माने में मोहब्बत के सिवा
                    राहतें और भी हैं वस्ल की राहत के सिवा
                """.trimIndent(),
                contentUrdu = """
                    مجھ سے پہلی سی محبت مری محبوب نہ مانگ
                    میں نے سمجھا تھا کہ تو ہے تو درخشاں ہے حیات

                    تیرا غم ہے تو غمِ دہر کا جھگڑا کیا ہے
                    تیری صورت سے ہے عالم میں بہاروں کو ثبات

                    اور بھی دکھ ہیں زمانے میں محبت کے سوا
                    راحتیں اور بھی ہیں وصل کی راحت کے سوا
                """.trimIndent(),
                englishMeaning = "Ask me not, my beloved, for the love we once shared; the world holds sorrows greater than love.",
                suggestedVoiceId = "mature_poet",
                suggestedEmotionId = "nostalgia",
                suggestedAmbienceId = "sufi_tanpura"
            ),
            ClassicPoem(
                title = "बे-दिली क्या यूँ ही (Be-Dili Kya Yun Hi)",
                poet = "Jaun Elia",
                era = "Modern Urdu Nihilism & Passion",
                scriptType = "Hindi-Urdu",
                content = """
                    बे-दिली क्या यूँ ही दिन गुज़र जाएँगे
                    सिर्फ़ ज़िंदा रहे हम तो मर जाएँगे

                    ख़ामशी कह रही है कि अब बात कर
                    वर्ना हम अपने अंदर उतर जाएँगे

                    इक अजब बे-क़रारी है हर साँस में
                    हम अगर थाम लेंगे तो बिखर जाएँगे
                """.trimIndent(),
                contentUrdu = """
                    بے دلی کیا یوں ہی دن گزر جائیں گے
                    صرف زندہ رہے ہم تو مر جائیں گے

                    خامشی کہہ رہی ہے کہ اب بات کر
                    ورنہ ہم اپنے اندر اتر جائیں گے

                    اک عجب بے قراری ہے ہر سانس میں
                    ہم اگر تھام لیں گے تو بکھر جائیں گے
                """.trimIndent(),
                englishMeaning = "Will our days pass in such joyless detachment? If we merely survive, surely we shall die.",
                suggestedVoiceId = "melancholic_sad",
                suggestedEmotionId = "loneliness",
                suggestedAmbienceId = "late_night_rain"
            ),
            ClassicPoem(
                title = "अगर ख़िलाफ़ हैं होने दो (Agar Khilaaf Hain)",
                poet = "Rahat Indori",
                era = "Contemporary Mushaira Legend",
                scriptType = "Hindi-Urdu",
                content = """
                    अगर ख़िलाफ़ हैं होने दो जान थोड़ी है
                    ये सब धुआँ है कोई आसमाँ थोड़ी है

                    लगेगी आग तो आएँगे घर कई ज़द में
                    यहाँ पे सिर्फ़ हमारा मकान थोड़ी है

                    मैं जानता हूँ कि दुश्मन भी कम नहीं लेकिन
                    हमारी तरह हथेली पे जान थोड़ी है

                    सभी का ख़ून है शामिल यहाँ की मिट्टी में
                    किसी के बाप का हिन्दोस्तान थोड़ी है
                """.trimIndent(),
                contentUrdu = """
                    اگر خلاف ہیں ہونے دو جان تھوڑی ہے
                    یہ سب دھواں ہے کوئی آسماں تھوڑی ہے

                    لگے گی آگ تو آئیں گے گھر کئی زد میں
                    یہاں پہ صرف ہمارا مکان تھوڑی ہے

                    سبھی کا خون ہے شامل یہاں کی مٹی میں
                    کسی کے باپ کا ہندوستان تھوڑی ہے
                """.trimIndent(),
                englishMeaning = "If they oppose, let them; my spirit cannot be quelled. The soil belongs to all our blood.",
                suggestedVoiceId = "powerful_dramatic",
                suggestedEmotionId = "intensity",
                suggestedAmbienceId = "cinematic_atmosphere"
            ),
            ClassicPoem(
                title = "ख़ुदी को कर बुलंद इतना (Khudi Ko Kar Buland)",
                poet = "Allama Muhammad Iqbal",
                era = "Philosophical Renaissance",
                scriptType = "Hindi-Urdu",
                content = """
                    ख़ुदी को कर बुलंद इतना कि हर तक़दीर से पहले
                    ख़ुदा बंदे से ख़ुद पूछे बता तेरी रज़ा क्या है

                    सितारों से आगे जहाँ और भी हैं
                    अभी इश्क़ के इम्तिहाँ और भी हैं

                    तू शाहीं है परवाज़ है काम तेरा
                    तेरे सामने आसमाँ और भी हैं
                """.trimIndent(),
                contentUrdu = """
                    خودی کو کر بلند اتنا کہ ہر تقدیر سے پہلے
                    خدا بندے سے خود پوچھے بتا تیری رضا کیا ہے

                    ستاروں سے آگے جہاں اور بھی ہیں
                    ابھی عشق کے امتحاں اور بھی ہیں

                    تو شاہیں ہے پرواز ہے کام تیرا
                    تیرے سامنے آسماں اور بھی ہیں
                """.trimIndent(),
                englishMeaning = "Exalt your Self so high that before every decree of destiny, God Himself shall ask: Tell me, what is thy wish?",
                suggestedVoiceId = "passionate_poetic",
                suggestedEmotionId = "hope",
                suggestedAmbienceId = "cinematic_atmosphere"
            ),
            ClassicPoem(
                title = "हो गई है पीर पर्वत-सी (Peer Parvat-Si)",
                poet = "Dushyant Kumar",
                era = "Hindi Ghazal Revolution",
                scriptType = "Hindi",
                content = """
                    हो गई है पीर पर्वत-सी पिघलनी चाहिए
                    इस हिमालय से कोई गंगा निकलनी चाहिए

                    आज यह दीवार, परदों की तरह हिलने लगी
                    शर्त लेकिन थी कि ये बुनियाद हिलनी चाहिए

                    सिर्फ़ हंगामा खड़ा करना मेरा मक़सद नहीं
                    मेरी कोशिश है कि ये सूरत बदलनी चाहिए

                    मेरे सीने में नहीं तो तेरे सीने में सही
                    हो कहीं भी आग, लेकिन आग जलनी चाहिए
                """.trimIndent(),
                contentUrdu = "",
                englishMeaning = "The pain has grown mountain-high, it must melt away. Somewhere the flame of truth must burn.",
                suggestedVoiceId = "passionate_poetic",
                suggestedEmotionId = "intensity",
                suggestedAmbienceId = "cinematic_atmosphere"
            ),
            ClassicPoem(
                title = "वक़्त रहता नहीं (Waqt Rehta Nahi)",
                poet = "Gulzar",
                era = "Contemporary Master",
                scriptType = "Hindustani",
                content = """
                    वक़्त रहता नहीं कहीं टिक कर
                    आदत इस की भी आदमी सी है

                    आइना देख कर तसल्ली हुई
                    हम को इस घर में जानता है कोई

                    हाथ छूटें भी तो रिश्ते नहीं छोड़ा करते
                    वक़्त की शाख़ से लम्हे नहीं तोड़ा करते
                """.trimIndent(),
                contentUrdu = "",
                englishMeaning = "Time never lingers still; its fickle nature is just like a mortal soul.",
                suggestedVoiceId = "warm_intimate",
                suggestedEmotionId = "reflection",
                suggestedAmbienceId = "minimal_piano"
            )
        )
    }
}
