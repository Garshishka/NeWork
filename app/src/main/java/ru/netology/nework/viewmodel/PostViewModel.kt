package ru.netology.nework.viewmodel

import android.net.Uri
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
import ru.netology.nework.dto.PhotoModel
import ru.netology.nework.dto.Post
import ru.netology.nework.repository.PostRepository
import ru.netology.nework.utils.SingleLiveEvent
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {
    val edited = MutableLiveData(empty)

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

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val _postCreatedError = SingleLiveEvent<Pair<String, Post>>()
    val postCreatedError: LiveData<Pair<String, Post>>
        get() = _postCreatedError
    private val _postsRemoveError = SingleLiveEvent<Pair<String, Long>>()
    val postsRemoveError: LiveData<Pair<String, Long>>
        get() = _postsRemoveError

    var draft = ""

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

    fun changeContent(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content == text) {
                return
            }
            edited.value = it.copy(content = text)
        }
    }

    fun save() = viewModelScope.launch {
        edited.value?.let {
            appAuth.getToken()?.let { token ->
                try {
                    when (_photo.value) {
                        noPhoto -> repository.save(it, token)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, file, token)
                        }
                    }
                    //appAuth.getToken()?.let { it1 -> repository.save(it, it1) }
                    _postCreated.postValue(Unit)
                    empty()
                } catch (e: Exception) {
                    println(e.message.toString())
                    _postCreatedError.postValue(e.message.toString() to it)
                }
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun empty() {
        edited.value = empty
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            appAuth.getToken()?.let { repository.removeById(it, id) }
        } catch (e: Exception) {
            println(e.message)
            _postsRemoveError.postValue(e.message.toString() to id)
        }
    }

    fun changePhoto(fileUri: Uri?, toFile: File?) {
        _photo.value = PhotoModel(fileUri, toFile)
    }

    fun deletePhoto() {
        _photo.value = noPhoto
    }
}

private val empty = Post(
    id = 0,
    content = "",
    author = "Me",
    authorAvatar = null,
    published = "",
)

private val noPhoto = PhotoModel(null, null)