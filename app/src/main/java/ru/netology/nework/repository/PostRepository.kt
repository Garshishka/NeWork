package ru.netology.nework.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Post
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<Post>>

    suspend fun removeById(authToken: String, id: Long)

    suspend fun save(post: Post, authToken: String)

    suspend fun getAll()

    suspend fun saveWithAttachment(post: Post, file: File, authToken: String, attachmentType: AttachmentType)
}