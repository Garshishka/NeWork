package ru.netology.nework.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE show = 1 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE notOnServer = 1")
    suspend fun getAllUnsent(): List<PostEntity>

    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentByID(id: Int, content: String)

    suspend fun save(post: PostEntity) =
        if (post.id == 0) insert(post) else updateContentByID(
            post.id,
            post.content
        )

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Int): PostEntity

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    suspend fun likeById(id: Int, userId: Int) {
        val post = getById(id)
        val likesList = post.likeOwnerIds as MutableList<Int>
        if (post.likedByMe) {
            likesList.remove(userId)
        } else {
            likesList.add(userId)
        }
        insert(post.copy(likeOwnerIds = likesList, likedByMe = !post.likedByMe))
    }

    @Query("DELETE FROM PostEntity")
    suspend fun clear()
}