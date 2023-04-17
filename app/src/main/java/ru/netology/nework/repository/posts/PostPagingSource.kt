package ru.netology.nework.repository.posts

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.Post
import java.io.IOException

class PostPagingSource(private val apiService: ApiService) : PagingSource<Int, Post>(){
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        try {
            val result = when (params) {
                is LoadParams.Refresh -> {
                    apiService.getLatest(params.loadSize)
                }
                is LoadParams.Append -> {
                    apiService.getBefore(count = params.loadSize, id = params.key.toString(),)
                }
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(), nextKey = null, prevKey = params.key
                )
            }

            if (!result.isSuccessful) {
                throw HttpException(result)
            }

            val data = result.body().orEmpty()
            return LoadResult.Page(data = data, prevKey = params.key, data.lastOrNull()?.id)
        }
        catch (e: IOException){
            return LoadResult.Error(e)
        }
    }
}
