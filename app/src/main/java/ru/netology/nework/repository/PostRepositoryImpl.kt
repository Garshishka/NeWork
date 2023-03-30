package ru.netology.nework.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.PostDao
import ru.netology.nework.dao.PostRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.Attachment
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.MediaUpload
import ru.netology.nework.dto.Post
import ru.netology.nework.entity.PostEntity
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = PostRemoteMediator(apiService, postDao, postRemoteKeyDao, appDb),
//        pagingSourceFactory = {
//            PostPagingSource(
//                apiService
//            )
//        }
    ).flow
        .map { it.map(PostEntity::toDto) }

    override suspend fun removeById(authToken: String, id: Long) {
        val removed = postDao.getById(id)
        postDao.removeById(id)
        try {
            val response = apiService.removeById(authToken, id)
            if (!response.isSuccessful) {
                postDao.insert(removed)
                throw RuntimeException(response.code().toString())
            }
        } catch (e: Exception) {
            postDao.insert(removed)
            throw RuntimeException(e)
        }
    }

    override suspend fun save(post: Post, authToken: String) {
        postDao.save(PostEntity.fromDto(post, true))
        try {
            val response = apiService.save(authToken, post)
            if (!response.isSuccessful) {
                throw RuntimeException(
                    response.code().toString()
                )
            }
            val body = response.body() ?: throw RuntimeException("body is null")
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override suspend fun likeById(id: Long, willLike: Boolean, authToken: String): Post {
        postDao.likeById(id)
        try {
            val response = if (willLike)
                apiService.likeById(authToken, id)
            else
                apiService.dislikeById(authToken, id)
            if (!response.isSuccessful) {
                postDao.likeById(id)
                throw RuntimeException(response.code().toString())
            }
            return response.body() ?: throw RuntimeException("body is null")
        } catch (e: Exception) {
            postDao.likeById(id)
            throw RuntimeException(e)
        }
    }

    override suspend fun getAll(authToken: String?) {
        val response = apiService.getAll()
        if (!response.isSuccessful) {
            throw RuntimeException(response.code().toString())
        }
        val posts = response.body() ?: throw RuntimeException("body is null")
        postDao.insert(posts.map(PostEntity.Companion::fromDto))

        if (authToken != null) {
            postDao.getAllUnsent().forEach { save(it.toDto(), authToken) }
        }
    }

    override suspend fun saveWithAttachment(
        post: Post,
        file: File,
        authToken: String,
        attachmentType: AttachmentType
    ) {
        try {
            val upload = upload(file, authToken)
            val postWithAttachment =
                post.copy(attachment = Attachment(upload.url, attachmentType))
            save(postWithAttachment, authToken)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private suspend fun upload(file: File, authToken: String): MediaUpload {
        try {
            val data =
                MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())

            val response = apiService.upload(authToken, data)
            if (!response.isSuccessful) {
                throw RuntimeException(
                    response.code().toString()
                )
            }
            return response.body() ?: throw RuntimeException("body is null")
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}