package ru.netology.nework.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.User
import java.io.File

interface PostRepository {
    val data: Flow<PagingData<Post>>
    val dataMyWall: Flow<PagingData<Post>>
    val usersData: LiveData<List<User>>
    suspend fun getAll(authToken: String?)
    suspend fun getMyWall(authToken: String, userId: Int)
    suspend fun removeById(authToken: String, id: Int)
    suspend fun save(post: Post, authToken: String)
    suspend fun likeById(id: Int, willLike: Boolean, authToken: String, userId: Int): Post
    suspend fun saveWithAttachment(post: Post, file: File, authToken: String, attachmentType: AttachmentType)
    suspend fun getUsers()
    suspend fun getBackOldUsers(oldUsers: List<User>)
    suspend fun changeCheckedUsers(id: Int, changeToOtherState: Boolean)
}