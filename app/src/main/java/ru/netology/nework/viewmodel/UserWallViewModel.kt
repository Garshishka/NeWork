package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.FeedModelState
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.PostRepository
import java.time.LocalDateTime
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class UserWallViewModel @Inject constructor(
    repository: PostRepository,
    apiService: ApiService,
    appAuth: AppAuth
) : PostViewModel(repository, apiService, appAuth) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val data: Flow<PagingData<Post>> = appAuth
        .state
        .map { it?.id }
        .flatMapLatest { id ->
            repository.dataMyWall.cachedIn(viewModelScope)
                .map { posts ->
                    posts.map { post ->
                        post.copy(ownedByMe = post.authorId == id)
                    }
                }
        }.flowOn(Dispatchers.Default)

    private val _myJob = MutableLiveData<String>("")
    val myJob: LiveData<String>
        get() = _myJob//for showing on user info card when only your posts are shown

    override fun load() = viewModelScope.launch {
        _dataState.value = FeedModelState.Loading
        try {
            appAuth.getToken()?.let { repository.getMyWall(it, appAuth.getId()) }
                ?: throw RuntimeException("Can't get auth token")
            _dataState.value = FeedModelState.Idle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }

    fun getMyJob() = viewModelScope.launch {
        appAuth.getToken()?.let { token ->
            try {
                val response = apiService.getMyJobs(token)
                if (!response.isSuccessful) {
                    throw RuntimeException(response.code().toString())
                }
                val jobs = response.body() ?: throw RuntimeException("body is null")
                val pack = mutableMapOf<LocalDateTime, String>()
                jobs.forEach() {
                    pack.put(
                        OffsetDateTime.parse(it.start).toLocalDateTime(),
                        it.name
                    )
                }
                _myJob.postValue(pack.maxBy { it.key }.value)
            } catch (e: Exception) {
                println(e.message)
                _myJob.postValue("")
            }
        }
    }
}