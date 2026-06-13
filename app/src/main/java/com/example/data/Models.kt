package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey val id: String,
    val username: String,
    val bio: String,
    val avatarUrl: String,
    val coverUrl: String,
    val followersCount: Int,
    val followingCount: Int,
    val likesCount: Int,
    val dreamCount: Int,
    val isCurrentUser: Boolean = false,
    val isAdmin: Boolean = false,
    val badgeName: String? = null,
    val isPremium: Boolean = false
)

@Entity(tableName = "dream_posts")
data class DreamPost(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val authorUsername: String,
    val authorAvatar: String,
    val originalText: String,
    val title: String,
    val aiStory: String,
    val aiInterpretation: String,
    val aiImagePrompt: String,
    val aiImageSeed: Long = 0L,
    val moodTags: String, // comma-separated list
    val alternateEndings: String, // newline-separated or JSON
    val likesCount: Int,
    val hasLiked: Boolean = false,
    val bookmarksCount: Int,
    val hasBookmarked: Boolean = false,
    val repostsCount: Int,
    val hasReposted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val isDraft: Boolean = false,
    val ambientTrack: String = "Luminous Atmosphere",
    val communityId: String? = null
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dreamId: Long,
    val authorUsername: String,
    val authorAvatar: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "communities")
data class Community(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val bgColors: String, // comma-separated hex values
    val membersCount: Int,
    val isJoined: Boolean = false
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: String,
    val senderUsername: String,
    val senderAvatar: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val stickerName: String? = null
)

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // "LIKE", "COMMENT", "FOLLOW", "AI_COMPLETED"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
