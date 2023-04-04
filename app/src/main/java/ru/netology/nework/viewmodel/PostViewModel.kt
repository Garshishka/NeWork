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
import ru.netology.nework.dto.*
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
//    val editedLink = MutableLiveData(empty)
//    val editedCoords = MutableLiveData(empty)

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

    private val _attachmnet = MutableLiveData(noMedia)
    val attachment: LiveData<MediaModel>
        get() = _attachmnet

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    private val _postCreatedError = SingleLiveEvent<Pair<String, Post>>()
    val postCreatedError: LiveData<Pair<String, Post>>
        get() = _postCreatedError
    private val _postsRemoveError = SingleLiveEvent<Pair<String, Int>>()
    val postsRemoveError: LiveData<Pair<String, Int>>
        get() = _postsRemoveError
    private val _postsLikeError = SingleLiveEvent<Pair<String, Pair<Int, Boolean>>>()
    val postsLikeError: LiveData<Pair<String, Pair<Int, Boolean>>>
        get() = _postsLikeError

    var draft = ""
    var draftLink = ""
    var draftCoordsLat = ""
    var draftCoordsLong = ""

    init {
        load()
    }

    fun load(isRefreshing: Boolean = false) = viewModelScope.launch {
        _dataState.value = if (isRefreshing) FeedModelState.Refreshing else FeedModelState.Loading
        try {
            repository.getAll(appAuth.getToken())
            _dataState.value = FeedModelState.Idle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }

    fun likeById(id: Int, likedByMe: Boolean) = viewModelScope.launch {
        try {
            appAuth.getToken()?.let { repository.likeById(id, !likedByMe, it, appAuth.getId()) }
        } catch (e: Exception) {
            _postsLikeError.postValue(e.toString() to (id to likedByMe))
        }
    }

    fun changeContent(content: String, link: String, coordsLat: String, coordsLong: String) {
        edited.value?.let {
            val text = content.trim()
            val textLink = link.trim()
            val textCoordsLat = coordsLat.trim()
            val textCoordsLong = coordsLong.trim()
            val coords = if (textCoordsLat.isNotBlank() && textCoordsLong.isNotBlank()) Coords(
                textCoordsLat,
                textCoordsLong
            ) else null
//            if (it.content == text) {
//                return
//            }
            edited.value =
                it.copy(
                    content = text,
                    link = if (textLink.isNotBlank()) textLink else null,
                    coords = coords,
                )
        }
    }

    fun save() = viewModelScope.launch {
        edited.value?.let {
            appAuth.getToken()?.let { token ->
                try {
                    when (_attachmnet.value) {
                        noMedia -> repository.save(it, token)
                        else -> _attachmnet.value?.file?.let { file ->
                            repository.saveWithAttachment(
                                it,
                                file,
                                token,
                                _attachmnet.value!!.attachmentType
                            )
                        }
                    }
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
        deleteMedia()
    }

    fun removeById(id: Int) = viewModelScope.launch {
        try {
            appAuth.getToken()?.let { repository.removeById(it, id) }
        } catch (e: Exception) {
            println(e.message)
            _postsRemoveError.postValue(e.message.toString() to id)
        }
    }

    fun changeMedia(fileUri: Uri?, toFile: File?, attachmentType: AttachmentType) {
        _attachmnet.value = MediaModel(fileUri, toFile, attachmentType)
    }

    fun deleteMedia() {
        _attachmnet.value = noMedia
    }
}

private val empty = Post(
    id = 0,
    content = "",
    author = "Me",
    authorAvatar = null,
    published = "",
)

private val noMedia = MediaModel(null, null, AttachmentType.NONE)