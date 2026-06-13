package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts ORDER BY isCurrentUser DESC")
    fun getAllAccounts(): Flow<List<UserAccount>>

    @Query("SELECT * FROM user_accounts WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserAccount?>

    @Query("SELECT * FROM user_accounts WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(userAccount: UserAccount)

    @Update
    suspend fun updateAccount(userAccount: UserAccount)

    @Query("UPDATE user_accounts SET followersCount = followersCount + 1 WHERE id = :userId")
    suspend fun incrementFollowers(userId: String)

    @Query("UPDATE user_accounts SET followersCount = followersCount - 1 WHERE id = :userId")
    suspend fun decrementFollowers(userId: String)
}

@Dao
interface DreamPostDao {
    @Query("SELECT * FROM dream_posts WHERE isDraft = 0 ORDER BY timestamp DESC")
    fun getFeedFlow(): Flow<List<DreamPost>>

    @Query("SELECT * FROM dream_posts WHERE isDraft = 0 AND communityId IS NULL ORDER BY timestamp DESC")
    fun getFeedWithoutCommunities(): Flow<List<DreamPost>>

    @Query("SELECT * FROM dream_posts WHERE isDraft = 0 AND communityId = :communityId ORDER BY timestamp DESC")
    fun getCommunityFeed(communityId: String): Flow<List<DreamPost>>

    @Query("SELECT * FROM dream_posts WHERE isDraft = 1 ORDER BY timestamp DESC")
    fun getDraftsFlow(): Flow<List<DreamPost>>

    @Query("SELECT * FROM dream_posts WHERE hasBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarksFlow(): Flow<List<DreamPost>>

    @Query("SELECT * FROM dream_posts WHERE userId = :userId AND isDraft = 0 ORDER BY timestamp DESC")
    fun getDreamsByUserFlow(userId: String): Flow<List<DreamPost>>

    @Query("SELECT * FROM dream_posts WHERE id = :id LIMIT 1")
    suspend fun getDreamById(id: Long): DreamPost?

    @Query("SELECT * FROM dream_posts WHERE title LIKE :query OR originalText LIKE :query OR aiStory LIKE :query OR moodTags LIKE :query ORDER BY timestamp DESC")
    fun searchDreams(query: String): Flow<List<DreamPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDream(dreamPost: DreamPost): Long

    @Update
    suspend fun updateDream(dreamPost: DreamPost)

    @Query("DELETE FROM dream_posts WHERE id = :id")
    suspend fun deleteDream(id: Long)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE dreamId = :dreamId ORDER BY timestamp ASC")
    fun getCommentsByDreamFlow(dreamId: Long): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM communities")
    fun getAllCommunitiesFlow(): Flow<List<Community>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunity(community: Community)

    @Query("UPDATE communities SET isJoined = :isJoined, membersCount = membersCount + (CASE WHEN :isJoined = 1 THEN 1 ELSE -1 END) WHERE id = :id")
    suspend fun toggleJoin(id: String, isJoined: Boolean)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getNotificationsFlow(): Flow<List<NotificationItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Database(
    entities = [
        UserAccount::class,
        DreamPost::class,
        Comment::class,
        Community::class,
        ChatMessage::class,
        NotificationItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
    abstract fun dreamPostDao(): DreamPostDao
    abstract fun commentDao(): CommentDao
    abstract fun communityDao(): CommunityDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "somnio_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
