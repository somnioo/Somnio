package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenStatus {
    SPLASH, ONBOARDING, AUTH_LOGIN, AUTH_REGISTER, MAIN
}

enum class ActiveScreen {
    HOME, EXPLORE, CREATE, NOTIFICATIONS, PROFILE,
    MESSAGES, COMMUNITIES, BOOKMARKS, CREATOR_STUDIO, SETTINGS, ADMIN, HELP
}

data class AuthState(
    val email: String = "",
    val isLoggedIn: Boolean = false,
    val hasVerifyingPass: Boolean = false,
    val error: String? = null
)

data class DreamAiStats(
    val totalSimulations: Int = 1840,
    val averageEngagement: Float = 94.2f,
    val totalRemixes: Int = 124,
    val activeNexusNodes: Int = 42
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DreamViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = DreamRepository(db)

    // Navigation and UX Flow
    private val _screenStatus = MutableStateFlow(ScreenStatus.SPLASH)
    val screenStatus: StateFlow<ScreenStatus> = _screenStatus.asStateFlow()

    private val _activeScreen = MutableStateFlow(ActiveScreen.HOME)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    // Auth flows
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Database flows
    val currentUser = repository.currentUserFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val feed = repository.feed.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val drafts = repository.drafts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val bookmarks = repository.bookmarks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val communities = repository.communities.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val chatMessages = repository.allMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val notifications = repository.notifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and Categorization
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMoodFilter = MutableStateFlow<String?>(null)
    val selectedMoodFilter: StateFlow<String?> = _selectedMoodFilter.asStateFlow()

    val filteredFeed: StateFlow<List<DreamPost>> = combine(feed, _searchQuery, _selectedMoodFilter) { feedList, query, mood ->
        var list = feedList
        if (query.isNotEmpty()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.originalText.contains(query, ignoreCase = true) ||
                it.aiStory.contains(query, ignoreCase = true) ||
                it.moodTags.contains(query, ignoreCase = true)
            }
        }
        if (mood != null) {
            list = list.filter { it.moodTags.contains(mood, ignoreCase = true) }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Creation states
    private val _dreamPromptInput = MutableStateFlow("")
    val dreamPromptInput: StateFlow<String> = _dreamPromptInput.asStateFlow()

    private val _aiExpansionLoading = MutableStateFlow(false)
    val aiExpansionLoading: StateFlow<Boolean> = _aiExpansionLoading.asStateFlow()

    private val _scannedDreamResult = MutableStateFlow<ExpandedDreamJson?>(null)
    val scannedDreamResult: StateFlow<ExpandedDreamJson?> = _scannedDreamResult.asStateFlow()

    // Comment modal
    private val _activeCommentDreamId = MutableStateFlow<Long?>(null)
    val activeCommentDreamId: StateFlow<Long?> = _activeCommentDreamId.asStateFlow()

    val activeComments: StateFlow<List<Comment>> = _activeCommentDreamId.flatMapLatest { id ->
        if (id != null) repository.getCommentsForDream(id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Communities and Collaborative Rooms state
    private val _selectedCommunityForFeed = MutableStateFlow<Community?>(null)
    val selectedCommunityForFeed: StateFlow<Community?> = _selectedCommunityForFeed.asStateFlow()

    val communityFeedList: StateFlow<List<DreamPost>> = _selectedCommunityForFeed.flatMapLatest { comm ->
        if (comm != null) repository.getCommunityFeed(comm.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin state configurations
    private val _systemMonitoringEnabled = MutableStateFlow(true)
    val systemMonitoringEnabled: StateFlow<Boolean> = _systemMonitoringEnabled.asStateFlow()

    private val _aiProvider = MutableStateFlow("Gemini (Auto)")
    val aiProvider: StateFlow<String> = _aiProvider.asStateFlow()

    private val _userAccountsList = db.userAccountDao().getAllAccounts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val userAccountsList: StateFlow<List<UserAccount>> = _userAccountsList

    // Creator studio simulation stats
    private val _creatorStats = MutableStateFlow(DreamAiStats())
    val creatorStats: StateFlow<DreamAiStats> = _creatorStats.asStateFlow()

    init {
        // Run database pre-population
        viewModelScope.launch {
            repository.preloadIfEmpty()
        }

        // Animated Splash Screen timer
        viewModelScope.launch {
            delay(2800)
            val currUser = db.userAccountDao().getCurrentUser()
            if (currUser != null && currUser.username != "Somnio_Explorer") {
                // Already authenticated state
                _authState.update { it.copy(isLoggedIn = true, email = "authenticated@somnio.nexus") }
                _screenStatus.value = ScreenStatus.MAIN
            } else {
                _screenStatus.value = ScreenStatus.ONBOARDING
            }
        }
    }

    // Navigation togglers
    fun changeScreen(screen: ScreenStatus) {
        _screenStatus.value = screen
    }

    fun changeActiveTab(screen: ActiveScreen) {
        _activeScreen.value = screen
    }

    // Authentications
    fun performLogin(email: String, register: Boolean = false) {
        viewModelScope.launch {
            if (email.isBlank() || !email.contains("@")) {
                _authState.update { it.copy(error = "Please enter a valid dream-address.") }
                return@launch
            }
            _authState.update { it.copy(error = null, email = email, isLoggedIn = true) }

            // Ensure custom account exists in database
            val sanitizedUser = email.substringBefore("@").replace(".", "_")
            val existing = db.userAccountDao().getCurrentUser()
            if (existing != null) {
                db.userAccountDao().updateAccount(
                    existing.copy(
                        username = sanitizedUser,
                        bio = "Auth walk via Somnio. Mainframe calibrated."
                    )
                )
            }
            _screenStatus.value = ScreenStatus.MAIN
        }
    }

    fun performRegister(email: String, username: String) {
        viewModelScope.launch {
            if (email.isBlank() || !email.contains("@") || username.isBlank()) {
                _authState.update { it.copy(error = "Details incorrect. Enter high-frequency credentials.") }
                return@launch
            }
            _authState.update { it.copy(error = null, email = email, isLoggedIn = true) }
            val existing = db.userAccountDao().getCurrentUser()
            if (existing != null) {
                db.userAccountDao().updateAccount(
                    existing.copy(
                        username = username,
                        bio = "Pristine consciousness registered. Welcome to Somnio."
                    )
                )
            }
            _screenStatus.value = ScreenStatus.MAIN
        }
    }

    fun performLogout() {
        viewModelScope.launch {
            _authState.update { AuthState() }
            _activeScreen.value = ActiveScreen.HOME
            _screenStatus.value = ScreenStatus.AUTH_LOGIN
        }
    }

    fun deleteProfileAccount() {
        viewModelScope.launch {
            val user = db.userAccountDao().getCurrentUser()
            if (user != null) {
                // Reset bio/state
                db.userAccountDao().updateAccount(
                    user.copy(
                        username = "Somnio_Explorer",
                        bio = "Interstellar dreamwalker, chasing retro-futuristic fantasy lines in Jetpack Compose.",
                        followersCount = 142,
                        isPremium = false
                    )
                )
            }
            performLogout()
        }
    }

    // Searching / Filtering
    fun updateQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectMoodFilter(mood: String?) {
        _selectedMoodFilter.value = mood
    }

    // Dream Creation & AI Simulation Processing
    fun updatePromptInput(text: String) {
        _dreamPromptInput.value = text
    }

    fun editProfile(bio: String, username: String) {
        viewModelScope.launch {
            val user = db.userAccountDao().getCurrentUser() ?: return@launch
            db.userAccountDao().updateAccount(user.copy(username = username, bio = bio))
        }
    }

    fun triggerAiDreamExpansion() {
        val prompt = _dreamPromptInput.value.trim()
        if (prompt.isEmpty()) return

        viewModelScope.launch {
            _aiExpansionLoading.value = true
            try {
                val result = repository.expandDreamWithAI(prompt)
                _scannedDreamResult.value = result
            } catch (e: Exception) {
                _scannedDreamResult.value = null
            } finally {
                _aiExpansionLoading.value = false
            }
        }
    }

    fun clearCreatedResult() {
        _scannedDreamResult.value = null
    }

    fun publishGeneratedDream() {
        val result = _scannedDreamResult.value ?: return
        viewModelScope.launch {
            val user = db.userAccountDao().getCurrentUser() ?: return@launch
            val newPost = DreamPost(
                userId = user.id,
                authorUsername = user.username,
                authorAvatar = user.avatarUrl,
                title = result.title,
                originalText = _dreamPromptInput.value,
                aiStory = result.aiStory,
                aiInterpretation = result.interpretation,
                aiImagePrompt = result.imagePrompt,
                aiImageSeed = (100..9999999).random().toLong(),
                moodTags = result.tags.joinToString(","),
                alternateEndings = result.alternateEndings.joinToString("\n"),
                likesCount = 0,
                bookmarksCount = 0,
                repostsCount = 0,
                isDraft = false,
                ambientTrack = result.ambientTrack,
                communityId = _selectedCommunityForFeed.value?.id
            )
            repository.insertDream(newPost)
            _dreamPromptInput.value = ""
            _scannedDreamResult.value = null
            _activeScreen.value = ActiveScreen.HOME

            // Update user dream stats
            db.userAccountDao().updateAccount(user.copy(dreamCount = user.dreamCount + 1))

            // Trigger a delayed comment simulation from another member
            delay(3500)
            val creators = listOf("Artemis_Dreamer", "Neon_Oracle_9", "Aurora_Subconscious")
            val activeFeed = db.dreamPostDao().getFeedWithoutCommunities().firstOrNull() ?: return@launch
            val latestId = activeFeed.firstOrNull()?.id ?: return@launch
            val commentText = listOf(
                "This wave alignment is outstanding! Can feel the atmospheric synthetic breeze.",
                "Stunning description. I navigated this exact vector yesterday.",
                "Fascinating! Merging this into my dream boards as we speak."
            ).random()
            db.commentDao().insertComment(
                Comment(
                    dreamId = latestId,
                    authorUsername = creators.random(),
                    authorAvatar = "neon_oracle",
                    text = commentText
                )
            )
            db.notificationDao().insertNotification(
                NotificationItem(
                    title = "New Nexus Interaction",
                    message = "Someone left a comment on your expanded world: \"$commentText\"",
                    type = "COMMENT"
                )
            )
        }
    }

    fun saveDraftDream() {
        val prompt = _dreamPromptInput.value.trim()
        if (prompt.isEmpty()) return
        
        viewModelScope.launch {
            val user = db.userAccountDao().getCurrentUser() ?: return@launch
            val draftPost = DreamPost(
                userId = user.id,
                authorUsername = user.username,
                authorAvatar = user.avatarUrl,
                title = "Draft Sandbox: " + prompt.take(20),
                originalText = prompt,
                aiStory = "Subconscious exploration in offline draft form...",
                aiInterpretation = "Draft interpretations require server syncing or publishing to active nexus.",
                aiImagePrompt = "A beautiful minimalist draft layout, locked folder, vector icon.",
                moodTags = "draft, sandbox",
                alternateEndings = "Draft states do not have split endings.",
                likesCount = 0,
                bookmarksCount = 0,
                repostsCount = 0,
                isDraft = true,
                ambientTrack = "Quiet Static Waves"
            )
            repository.insertDream(draftPost)
            _dreamPromptInput.value = ""
            _activeScreen.value = ActiveScreen.HOME
            
            db.notificationDao().insertNotification(
                NotificationItem(
                    title = "Draft Saved",
                    message = "Your dream draft has been mapped securely within local database cache.",
                    type = "AI_COMPLETED"
                )
            )
        }
    }

    // Engagement togglers
    fun toggleLike(dreamId: Long) {
        viewModelScope.launch {
            repository.toggleLike(dreamId)
        }
    }

    fun toggleBookmark(dreamId: Long) {
        viewModelScope.launch {
            repository.toggleBookmark(dreamId)
        }
    }

    // Comment controller modal opening
    fun selectCommentDream(dreamId: Long?) {
        _activeCommentDreamId.value = dreamId
    }

    fun postComment(dreamId: Long, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(dreamId, text)
        }
    }

    // Direct Messages chat panel
    fun sendChatMessage(text: String, sticker: String? = null) {
        viewModelScope.launch {
            repository.sendMessage(text, sticker)
        }
    }

    // Community feed controllers
    fun selectCommunityForFeed(community: Community?) {
         _selectedCommunityForFeed.value = community
    }

    fun toggleCommunityJoinState(communityId: String) {
        viewModelScope.launch {
            repository.toggleCommunityJoin(communityId)
        }
    }

    // Admin commands
    fun toggleMonitoring() {
        _systemMonitoringEnabled.value = !_systemMonitoringEnabled.value
    }

    fun changeAiProvider(provider: String) {
        _aiProvider.value = provider
    }

    fun toggleUserAdmin(userId: String) {
        viewModelScope.launch {
            val existing = db.userAccountDao().getUserById(userId) ?: return@launch
            db.userAccountDao().updateAccount(existing.copy(isAdmin = !existing.isAdmin))
        }
    }

    fun toggleUserPremiumState(userId: String) {
        viewModelScope.launch {
            val existing = db.userAccountDao().getUserById(userId) ?: return@launch
            db.userAccountDao().updateAccount(existing.copy(isPremium = !existing.isPremium))
        }
    }

    fun deleteDreamFromFeed(postId: Long) {
        viewModelScope.launch {
            repository.deleteDream(postId)
        }
    }
}
