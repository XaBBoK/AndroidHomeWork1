package ru.netology.nmedia.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(id) FROM PostEntity WHERE visible = 0 ORDER BY id DESC")
    fun getInvisibleCount(): Long

    @Query("SELECT id FROM PostEntity WHERE visible = 0 ORDER BY id DESC LIMIT 1")
    fun getBiggestInvisibleId() : Long?

    @Query("UPDATE PostEntity SET visible = 1")
    suspend fun setAllVisible()

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long) : PostEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWithoutReplace(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWithoutReplace(posts: List<PostEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>): List<Long>

    @Transaction
    suspend fun clearAndInsert(posts: List<PostEntity>): List<Long> {
        clearAll()
        return insert(posts)
    }

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    @Query(
        """
            UPDATE PostEntity SET
                likes = likes + CASE WHEN likedByMe THEN 0 ELSE 1 END,
                likedByMe = 1
            WHERE id = :id
        """
    )
    suspend fun likeById(id: Long)

    @Query(
        """
            UPDATE PostEntity SET
                likes = likes - CASE WHEN likedByMe THEN 1 ELSE 0 END,
                likedByMe = 0
            WHERE id = :id
        """
    )
    suspend fun unlikeById(id: Long)

    @Query(
        """
            UPDATE PostEntity SET
                shares = shares + 10
            WHERE id = :id;
        """
    )
    suspend fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id= :id")
    suspend fun removeById(id: Long)

    suspend fun addOrEditPost(post: PostEntity) {
        insert(post)
    }

    @Query("DELETE FROM PostEntity")
    suspend fun clearAll()
}