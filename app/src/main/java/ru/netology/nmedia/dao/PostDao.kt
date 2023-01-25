package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.dto.NON_EXISTING_POST_ID
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity): Long

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    fun updateContentById(id: Long, content: String)

    @Query(
        """
            UPDATE PostEntity SET
                likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
            WHERE id = :id
        """
    )
    fun likeById(id: Long)

    @Query(
        """
            UPDATE PostEntity SET
                shares = shares + 10
            WHERE id = :id;
        """
    )
    fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id= :id")
    fun removeById(id: Long)

    fun addOrEditPost(post: PostEntity): Post {
        if (post.id == NON_EXISTING_POST_ID)
            return PostEntity.toDto(post.copy(id = insert(post)))
        else {
            updateContentById(post.id, post.content)
            return PostEntity.toDto(post)
        }
    }
}