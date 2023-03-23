package ru.netology.nework.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import ru.netology.nework.api.ApiService
import ru.netology.nework.dao.PostDao
import ru.netology.nework.entity.PostEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService
) : PostRepository {
    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            PostPagingSource(
                apiService
            )
        }
    ).flow

    override suspend fun getAll() {
        val response = apiService.getAll()
        if (!response.isSuccessful) {
            throw RuntimeException(response.code().toString())
        }
        val posts = response.body() ?: throw RuntimeException("body is null")
        postDao.insert(posts.map(PostEntity.Companion::fromDto))

       // postDao.getAllUnsent().forEach { save(it.toDto()) }
    }
}