package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.FeedModelState
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.PostRepository
import ru.netology.nework.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Post>> = appAuth
        .state
        .map { it?.id }
        .flatMapLatest { id ->
            repository.data.cachedIn(viewModelScope)
                .map { posts ->
                    posts.map { post ->
                        post.copy(ownedByMe = post.authorId == id)
                    }
                }
        }.flowOn(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>(FeedModelState.Idle)
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _postsRemoveError = SingleLiveEvent<Pair<String, Long>>()
    val postsRemoveError: LiveData<Pair<String, Long>>
        get() = _postsRemoveError

    init {
        load()
    }

    fun load(isRefreshing: Boolean = false) = viewModelScope.launch {
        _dataState.value = if (isRefreshing) FeedModelState.Refreshing else FeedModelState.Loading
        try {
            repository.getAll()
            _dataState.value = FeedModelState.Idle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            appAuth.getToken()?.let { repository.removeById(it, id) }
        } catch (e: Exception) {
            println(e.message)
            _postsRemoveError.postValue(e.message.toString() to id)
        }
    }
}