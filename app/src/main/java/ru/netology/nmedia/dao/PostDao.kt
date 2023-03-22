package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.netology.nmedia.dto.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Long) : PostEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

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