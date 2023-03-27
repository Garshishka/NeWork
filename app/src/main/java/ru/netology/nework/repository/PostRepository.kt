package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>

    suspend fun removeById(authToken: String, id: Long)

    suspend fun getAll()
}