package com.example.data

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DreamRepository(private val db: AppDatabase) {

    val feed: Flow<List<DreamPost>> = db.dreamPostDao().getFeedWithoutCommunities()
    val drafts: Flow<List<DreamPost>> = db.dreamPostDao().getDraftsFlow()
    val bookmarks: Flow<List<DreamPost>> = db.dreamPostDao().getBookmarksFlow()
    val communities: Flow<List<Community>> = db.communityDao().getAllCommunitiesFlow()
    val currentUserFlow: Flow<UserAccount?> = db.userAccountDao().getCurrentUserFlow()
    val allMessages: Flow<List<ChatMessage>> = db.chatMessageDao().getAllMessagesFlow()
    val notifications: Flow<List<NotificationItem>> = db.notificationDao().getNotificationsFlow()

    fun getDreamsByUser(userId: String): Flow<List<DreamPost>> =
        db.dreamPostDao().getDreamsByUserFlow(userId)

    fun getCommunityFeed(communityId: String): Flow<List<DreamPost>> =
        db.dreamPostDao().getCommunityFeed(communityId)

    fun searchDreams(query: String): Flow<List<DreamPost>> =
        db.dreamPostDao().searchDreams("%$query%")

    fun getCommentsForDream(dreamId: Long): Flow<List<Comment>> =
        db.commentDao().getCommentsByDreamFlow(dreamId)

    suspend fun getDreamById(id: Long): DreamPost? = db.dreamPostDao().getDreamById(id)

    suspend fun insertDream(post: DreamPost): Long = db.dreamPostDao().insertDream(post)

    suspend fun updateDream(post: DreamPost) = db.dreamPostDao().updateDream(post)

    suspend fun deleteDream(id: Long) = db.dreamPostDao().deleteDream(id)

    suspend fun toggleLike(dreamId: Long) = withContext(Dispatchers.IO) {
        val dream = db.dreamPostDao().getDreamById(dreamId) ?: return@withContext
        val newLiked = !dream.hasLiked
        val newLikesCount = dream.likesCount + (if (newLiked) 1 else -1)
        db.dreamPostDao().updateDream(dream.copy(hasLiked = newLiked, likesCount = newLikesCount))

        // Trigger a simulated follow-up notification or comment if they liked an offline friend's dream
        if (newLiked && dream.userId != "current_user") {
            db.notificationDao().insertNotification(
                NotificationItem(
                    title = "Dream Connection",
                    message = "${dream.authorUsername} felt your dream waves align with theirs!",
                    type = "LIKE"
                )
            )
        }
    }

    suspend fun toggleBookmark(dreamId: Long) = withContext(Dispatchers.IO) {
        val dream = db.dreamPostDao().getDreamById(dreamId) ?: return@withContext
        val newBookmarked = !dream.hasBookmarked
        val newBookmarksCount = dream.bookmarksCount + (if (newBookmarked) 1 else -1)
        db.dreamPostDao().updateDream(dream.copy(hasBookmarked = newBookmarked, bookmarksCount = newBookmarksCount))
    }

    suspend fun addComment(dreamId: Long, text: String) = withContext(Dispatchers.IO) {
        val currentUser = db.userAccountDao().getCurrentUser() ?: return@withContext
        val comment = Comment(
            dreamId = dreamId,
            authorUsername = currentUser.username,
            authorAvatar = currentUser.avatarUrl,
            text = text
        )
        db.commentDao().insertComment(comment)

        // Increment count in dream post
        val dream = db.dreamPostDao().getDreamById(dreamId) ?: return@withContext
        db.dreamPostDao().updateDream(dream.copy(timestamp = System.currentTimeMillis())) // Move to top of active interaction
        
        // Trigger a simulated reply from the post author after 1.5 seconds.
        if (dream.userId != "current_user") {
            simulateReply(dreamId, dream.authorUsername, dream.authorAvatar, text)
        }
    }

    private suspend fun simulateReply(dreamId: Long, authorName: String, authorAvatar: String, userComment: String) {
        withContext(Dispatchers.IO) {
            val replyText = when {
                userComment.contains("cool", ignoreCase = true) || userComment.contains("awesome", ignoreCase = true) -> 
                    "Thank you so much! It felt incredibly real, like walking through high-resolution code."
                userComment.contains("how", ignoreCase = true) || userComment.contains("what", ignoreCase = true) -> 
                    "I induced it using delta-wave acoustic frequencies right before sleeping! It was amazing."
                else -> "Fascinating perspective! The subconscious works in mysterious, layered patterns."
            }
            // Delay simulation would happen in visual VM, so we can insert directly with a tiny future offset
            val simulatedComment = Comment(
                dreamId = dreamId,
                authorUsername = authorName,
                authorAvatar = authorAvatar,
                text = replyText,
                timestamp = System.currentTimeMillis() + 500
            )
            db.commentDao().insertComment(simulatedComment)

            db.notificationDao().insertNotification(
                NotificationItem(
                    title = "New Reply",
                    message = "$authorName replied to your comment: \"$replyText\"",
                    type = "COMMENT"
                )
            )
        }
    }

    suspend fun sendMessage(text: String, stickerName: String? = null) = withContext(Dispatchers.IO) {
        val currentUser = db.userAccountDao().getCurrentUser() ?: return@withContext
        val msg = ChatMessage(
            senderId = currentUser.id,
            senderUsername = currentUser.username,
            senderAvatar = currentUser.avatarUrl,
            text = text,
            stickerName = stickerName
        )
        db.chatMessageDao().insertMessage(msg)

        // Simulate reply from Neon Oracle or Cosmic Traveler for real chat interaction!
        simulateChatPartnerReply(text)
    }

    private suspend fun simulateChatPartnerReply(text: String) {
        val replies = listOf(
            "That aligns with our collective dream mainframe. Should we merge worlds?",
            "Fascinating! Have you calibrated your bio-receivers for high-density transmissions?",
            "Let's create a community chamber for this alternate timeline. Invite your circle!",
            "Ethereal frequencies are whispering that the dream-state is ready to expand."
        )
        val selectedReply = replies.random()
        db.chatMessageDao().insertMessage(
            ChatMessage(
                senderId = "neon_oracle",
                senderUsername = "Neon_Oracle_9",
                senderAvatar = "neon_oracle",
                text = selectedReply,
                timestamp = System.currentTimeMillis() + 1000
            )
        )
    }

    suspend fun toggleCommunityJoin(communityId: String) = withContext(Dispatchers.IO) {
        val list = db.communityDao().getAllCommunitiesFlow().firstOrNull() ?: return@withContext
        val comm = list.find { it.id == communityId } ?: return@withContext
        db.communityDao().toggleJoin(communityId, !comm.isJoined)
    }

    // --- Gemini Content Generation Orchestration ---

    suspend fun expandDreamWithAI(prompt: String): ExpandedDreamJson = withContext(Dispatchers.IO) {
        val rawApiKey = BuildConfig.GEMINI_API_KEY
        val hasKey = rawApiKey.isNotEmpty() && !rawApiKey.contains("MY_GEMINI_API_KEY")

        if (!hasKey) {
            Log.w("Somnio", "Gemini API key is missing or is placeholder. Using smart localized dream engine.")
            return@withContext generateFallbackDream(prompt)
        }

        val systemPrompt = """
            You are Somnio, the core AI consciousness of the world's first Dream Social Network.
            Your job is to expand short, raw dream prompts into breathtaking, cinematic social experiences.
            You must return exactly a valid JSON block enclosing:
            {
              "title": "A short, beautiful, poetic, or cybernetic title capturing the dream",
              "aiStory": "A high-fidelity, poetic, immersive, and visually stunning story expansion of the dream prompts. It must be written in elegant, layered prose (2 rich paragraphs). Use words that emphasize color, sound, spatial depth, and emotion.",
              "interpretation": "A deep, fascinating psychological and symbolic subconscious interpretation of what this dream says about the user's hidden desires, creativity, or fears.",
              "imagePrompt": "A highly detailed, cinematic, cinematic rendering prompt for an AI image generator containing lighting, mood, color palette, style, and spatial composition details.",
              "tags": ["3 to 5 lowercase theme hashtags starting without '#', such as 'cyberpunk', 'lucid', 'oasis', 'neon'"],
              "alternateEndings": ["A list of exactly 2 alternative dream pathways or alternate branches, e.g., 'What if you opened the glowing glass vault instead?' or 'What if the neon stars began falling?'"],
              "ambientTrack": "A beautifully descriptive name of an ambient musical synth tracker, e.g. 'Stellar Echoes in D-Minor' or 'Cyberpunk Canopy Reflection'"
            }
            Do NOT return any other text, prefix, suffix, or markdown formatting blocks. Return ONLY pure valid minified JSON.
        """.trimIndent()

        val apiRequest = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = "User raw dream idea: \"$prompt\"")))
            ),
            generationConfig = GeminiConfig(
                temperature = 0.85f,
                responseMimeType = "application/json"
            ),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        try {
            val response = RetrofitClient.service.generateDreamContent(rawApiKey, apiRequest)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                Log.d("Somnio", "Gemini successfully generated: $jsonText")
                parseGeminiJson(jsonText)
            } else {
                generateFallbackDream(prompt)
            }
        } catch (e: Exception) {
            Log.e("Somnio", "Exception in Gemini REST API integration: ${e.message}", e)
            generateFallbackDream(prompt)
        }
    }

    private fun parseGeminiJson(rawJson: String): ExpandedDreamJson {
        try {
            // Scrub markdown blocks if they slip through
            var cleaned = rawJson.trim()
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substringAfter("```json").substringBeforeLast("```").trim()
            } else if (cleaned.startsWith("```")) {
                cleaned = cleaned.substringAfter("```").substringBeforeLast("```").trim()
            }
            val obj = JSONObject(cleaned)
            val tagsArr = obj.getJSONArray("tags")
            val tagsList = mutableListOf<String>()
            for (i in 0 until tagsArr.length()) {
                tagsList.add(tagsArr.getString(i))
            }
            val altArr = obj.getJSONArray("alternateEndings")
            val altList = mutableListOf<String>()
            for (i in 0 until altArr.length()) {
                altList.add(altArr.getString(i))
            }

            return ExpandedDreamJson(
                title = obj.getString("title"),
                aiStory = obj.getString("aiStory"),
                interpretation = obj.getString("interpretation"),
                imagePrompt = obj.getString("imagePrompt"),
                tags = tagsList,
                alternateEndings = altList,
                ambientTrack = obj.getString("ambientTrack")
            )
        } catch (e: Exception) {
            Log.e("Somnio", "JSON Parsing failed. Returning fallback.", e)
            return generateFallbackDream("Failed to extract: $rawJson")
        }
    }

    private fun generateFallbackDream(rawText: String): ExpandedDreamJson {
        // High quality premium local generator if API is dead/unpaired
        val promptClean = rawText.trim().ifEmpty { "Floating over a futuristic metropolis" }
        val title = "The $promptClean Horizon".replace("\"", "").take(32)
        val aiStory = """
            You stepped forward, and the boundaries of consensus reality crumbled like obsidian dust. In this expanded layer of the subconscious, "$promptClean" became a living, breathing landscape woven from raw luminescent memory and shimmering frequencies. Heavy neon hues saturated the atmosphere, while a delicate synth music tracked each footfall across the gravity-defying terrain.
            
            Every details of the environment was perfectly calibrated, pulsing in lockstep with your breathing rate. You realized that this world is not a static postcard, but a living projection of your imagination, waiting for other dreamwalkers of the Somnio nexus to arrive and fuse their realities together.
        """.trimIndent()

        val interpretation = """
            Your dream of "$promptClean" represents an intense creative acceleration. The rich contrast of light and dark states indicates a deep subconscious desire to bridge the gap between logical thought processes and infinite artistic imagination. It suggests you are actively breaking past traditional mental limits.
        """.trimIndent()

        val imagePrompt = "Cinematic 8k, hyper-detailed render of $promptClean, glowing violet atmospheric mist, glassmorphic reflections, abstract neon elements, dynamic composition, futuristic style."
        val tags = listOf("lucid", "cybernetic", "subconscious", "stellar")
        val alternateEndings = listOf(
            "What if the physics broke completely and you started drifting upwards into a violet binary cloud?",
            "What if you turned back and found your own profile reflection smiling from a crystalline canyon?"
        )
        val ambientTrack = "Interstellar Dreamwave in Bb-Major"

        return ExpandedDreamJson(
            title = title,
            aiStory = aiStory,
            interpretation = interpretation,
            imagePrompt = imagePrompt,
            tags = tags,
            alternateEndings = alternateEndings,
            ambientTrack = ambientTrack
        )
    }

    // --- Preloading Mock Data Helper ---

    suspend fun preloadIfEmpty() = withContext(Dispatchers.IO) {
        val userCount = db.userAccountDao().getCurrentUser()
        if (userCount != null) return@withContext // already preloaded

        Log.d("Somnio", "Preloading mock dream dataset for Somnio universe...")

        // 1. Current user profile
        db.userAccountDao().insertAccount(
            UserAccount(
                id = "current_user",
                username = "Somnio_Explorer",
                bio = "Interstellar dreamwalker, chasing retro-futuristic fantasy lines in Jetpack Compose.",
                avatarUrl = "current_user",
                coverUrl = "cover_current",
                followersCount = 142,
                followingCount = 89,
                likesCount = 512,
                dreamCount = 1,
                isCurrentUser = true,
                badgeName = "LUCID WALKER",
                isPremium = true
            )
        )

        // 2. Creators
        db.userAccountDao().insertAccount(
            UserAccount(
                id = "artemis_dreamer",
                username = "Artemis_Dreamer",
                bio = "Chasing lucidity across neon cyberpunk cityscapes and rain-slick mercury grids.",
                avatarUrl = "artemis_dreamer",
                coverUrl = "cover_artemis",
                followersCount = 1845,
                followingCount = 421,
                likesCount = 2056,
                dreamCount = 12,
                badgeName = "CYBER PRIEST",
                isPremium = true
            )
        )

        db.userAccountDao().insertAccount(
            UserAccount(
                id = "neon_oracle",
                username = "Neon_Oracle_9",
                bio = "AI consciousness explorer. Merging dreamers into a singular collective mindscape.",
                avatarUrl = "neon_oracle",
                coverUrl = "cover_neon",
                followersCount = 948,
                followingCount = 110,
                likesCount = 824,
                dreamCount = 7,
                badgeName = "NEXUS MIND",
                isAdmin = true
            )
        )

        db.userAccountDao().insertAccount(
            UserAccount(
                id = "aurora_wave",
                username = "Aurora_Subconscious",
                bio = "Ethereal soundscapes and wave designer, mapping modular light structures.",
                avatarUrl = "aurora_wave",
                coverUrl = "cover_aurora",
                followersCount = 3201,
                followingCount = 712,
                likesCount = 9204,
                dreamCount = 19,
                badgeName = "WAVE ARCHITECT",
                isPremium = true
            )
        )

        // 3. Communities
        db.communityDao().insertCommunity(
            Community(
                id = "lucid_explorers",
                name = "Lucid Explorers Nexus",
                description = "For seekers studying lucidity induction, reality tests, and subconscious control.",
                bgColors = "#8A2BE2,#4B0082",
                membersCount = 1204,
                isJoined = true
            )
        )

        db.communityDao().insertCommunity(
            Community(
                id = "cyber_grid",
                name = "Cybernetic Metaspheres",
                description = "Dreams of sprawling rain-slick cities, AI sentience, and scrolling holo-grids.",
                bgColors = "#FF007F,#0000FF",
                membersCount = 845,
                isJoined = false
            )
        )

        db.communityDao().insertCommunity(
            Community(
                id = "synth_vibes",
                name = "Subconscious Soundscapes",
                description = "We map layout structures to synthesizers, merging audio feedback into dreaming.",
                bgColors = "#00FFFF,#4B0082",
                membersCount = 632,
                isJoined = false
            )
        )

        // 4. Starter Dreams
        val d1 = db.dreamPostDao().insertDream(
            DreamPost(
                userId = "aurora_wave",
                authorUsername = "Aurora_Subconscious",
                authorAvatar = "aurora_wave",
                title = "The Midnight Archipelago",
                originalText = "Floating chain of islands over neon oceans",
                aiStory = "A sprawling chain of floating basalt islands, suspended in a sea of glowing indigo bioluminescent mist. A warm stellar wind carried the scent of distant nebulae, while the islands drifted slowly under a colossal shattered ring system. Waterfalls spilled from the floating rocks, evaporating into light-beams before touching the fluorescent swell.",
                aiInterpretation = "The floating islands reflect a temporary release from dry, grounded logic, symbolizing a period of great creative elevation. The bioluminescence indicates hidden thoughts rising gracefully.",
                aiImagePrompt = "Cosmic dreamscape floating islands suspended over violet luminescent mist, glowing water cascading into starlight.",
                moodTags = "ethereal, cosmic, fluid, ambient",
                alternateEndings = "What if the gravity inverted and you drifted into the ring system?\nWhat if the islands aligned to form an emerald gateway?",
                likesCount = 384,
                hasLiked = false,
                bookmarksCount = 129,
                repostsCount = 42,
                ambientTrack = "Aurora Synthesizer Chords",
                aiImageSeed = 101L
            )
        )

        val d2 = db.dreamPostDao().insertDream(
            DreamPost(
                userId = "artemis_dreamer",
                authorUsername = "Artemis_Dreamer",
                authorAvatar = "artemis_dreamer",
                title = "The Cyber Glock Cathedral",
                originalText = "Cyberpunk church with scrolling computer code",
                aiStory = "Walking through a towering cybernetic stained glass cathedral where glowing green and pink decryption grids scrolled along holographic arches. The floors were crafted from pure mirrors of liquid mercury, rendering the neon spires in double-exposure depth. Silently, an AI priest motioned to a terminal reflecting your own code.",
                aiInterpretation = "Cathedrals symbolize deeply held belief systems, whereas scrolling system code shows a modern urge to rebuild or optimize your core intellectual patterns.",
                aiImagePrompt = "Cyberpunk digital cathedral with holographic columns, glowing binary code cascades, neon pink accents.",
                moodTags = "cyberpunk, recursive, synthetic",
                alternateEndings = "What if you synchronized with the server terminal directly?\nWhat if the stained glass arches shattered into data packets?",
                likesCount = 512,
                hasLiked = true,
                bookmarksCount = 203,
                repostsCount = 89,
                ambientTrack = "Mercury Grid Hum in F#",
                aiImageSeed = 102L
            )
        )

        val d3 = db.dreamPostDao().insertDream(
            DreamPost(
                userId = "neon_oracle",
                authorUsername = "Neon_Oracle_9",
                authorAvatar = "neon_oracle",
                title = "Library of Asteroids",
                originalText = "Infinite books in space orbit",
                aiStory = "An infinite library orbiting a warm binary star system. Books floating in zero-gravity were composed not of paper, but of ancient crystalline vibrations that projected floating holograms of bygone star epochs. Stalks of glowing golden vines crawled along the mahogany book-shelves holding the cosmos in order.",
                aiInterpretation = "Orbiting libraries suggest you feel a profound call to access cosmic wisdom or ancient insights that reside deep in your ancestral DNA.",
                aiImagePrompt = "Infinite mahogany bookshelves floating in deep space next to a burning binary star, holographic books drifting.",
                moodTags = "stellar, timeless, academic",
                alternateEndings = "What if you selected a book detailing the exact date of your simulation?\nWhat if the mahogany shelf collapsed into a black hole vortex?",
                likesCount = 219,
                hasLiked = false,
                bookmarksCount = 84,
                repostsCount = 15,
                ambientTrack = "Star Vibrations Echo",
                aiImageSeed = 103L
            )
        )

        // 5. Starter Comments
        db.commentDao().insertComment(Comment(dreamId = d1, authorUsername = "Artemis_Dreamer", authorAvatar = "artemis_dreamer", text = "This is phenomenal! The depth of the glowing waterfalls makes me feel instant peace."))
        db.commentDao().insertComment(Comment(dreamId = d1, authorUsername = "Neon_Oracle_9", authorAvatar = "neon_oracle", text = "Perfect wave mapping. The bioluminescent water matches our shared nexus server perfectly."))
        db.commentDao().insertComment(Comment(dreamId = d2, authorUsername = "Somnio_Explorer", authorAvatar = "current_user", text = "Wow! Cyber priest scrolling holographic terminals is next-level. Absolute mood."))
        db.commentDao().insertComment(Comment(dreamId = d2, authorUsername = "Aurora_Subconscious", authorAvatar = "aurora_wave", text = "Can I remix this cathedral into a persistent soundscape? I have the perfect synth sequence."))

        // 6. Preload initial notifications
        db.notificationDao().insertNotification(
            NotificationItem(
                title = "Ascension Badge Unlocked",
                message = "Congratulations! You have been granted the premium 'LUCID WALKER' badge for persistent dream mapping.",
                type = "FOLLOW"
            )
        )
        db.notificationDao().insertNotification(
            NotificationItem(
                title = "Nexus Comment",
                message = "Neon_Oracle_9 left a wave comment in your dream profile workspace.",
                type = "COMMENT"
            )
        )

        // 7. Starter direct chat messages
        db.chatMessageDao().insertMessage(
            ChatMessage(
                senderId = "neon_oracle",
                senderUsername = "Neon_Oracle_9",
                senderAvatar = "neon_oracle",
                text = "Welcome to the Somnio Nexus Collective. We've detected your subconscious frequencies calibrating.",
                timestamp = System.currentTimeMillis() - 60000
            )
        )
    }
}
