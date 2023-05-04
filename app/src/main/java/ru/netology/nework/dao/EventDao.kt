package ru.netology.nework.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity WHERE show = 1 ORDER BY id DESC")
    fun getAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM EventEntity WHERE notOnServer = 1")
    suspend fun getAllUnsent(): List<EventEntity>

    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, EventEntity>

    @Query("SELECT * FROM EventEntity WHERE authorId = :id ORDER BY id DESC")
    fun getMyWalLPagingSource(id: Int): PagingSource<Int, EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<EventEntity>)

    @Query("UPDATE EventEntity SET content = :content WHERE id = :id")
    suspend fun updateContentByID(id: Int, content: String)

    suspend fun save(event: EventEntity) =
        if (event.id == 0) insert(event) else updateContentByID(
            event.id,
            event.content
        )

    @Query("SELECT * FROM EventEntity WHERE id = :id")
    suspend fun getById(id: Int): EventEntity

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    suspend fun likeById(id: Int, userId: Int) {
        val event = getById(id)
        val likesList = event.likeOwnerIds as MutableList<Int>
        if (event.likedByMe) {
            likesList.remove(userId)
        } else {
            likesList.add(userId)
        }
        insert(event.copy(likeOwnerIds = likesList, likedByMe = !event.likedByMe))
    }

    suspend fun participateById(id: Int, userId: Int) {
        val event = getById(id)
        val participatedList = event.participantIds.toMutableList()
        if (event.participatedByMe) {
            participatedList.remove(userId)
        } else {
            participatedList.add(userId)
        }
        insert(
            event.copy(
                participantIds = participatedList,
                participatedByMe = !event.participatedByMe
            )
        )
    }

    @Query("DELETE FROM EventEntity")
    suspend fun clear()
}