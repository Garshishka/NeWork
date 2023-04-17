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
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dto.*
import ru.netology.nework.repository.events.EventRepository
import ru.netology.nework.repository.users.UsersRepository
import ru.netology.nework.utils.SingleLiveEvent
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val usersRepository: UsersRepository,
    private val apiService: ApiService,
    private val appAuth: AppAuth
) : ViewModel() {
    val edited = MutableLiveData(emptyEvent)

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Event>> = appAuth
        .state
        .map { it?.id }
        .flatMapLatest { id ->
            repository.data.cachedIn(viewModelScope)
                .map { events ->
                    events.map { event ->
                        event.copy(ownedByMe = event.authorId == id)
                    }
                }
        }.flowOn(Dispatchers.Default)

    val usersData = usersRepository.usersData

    private val _dataState = MutableLiveData<FeedModelState>(FeedModelState.Idle)
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _attachment = MutableLiveData(noMedia)
    val attachment: LiveData<MediaModel>
        get() = _attachment

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated
    private val _eventCreatedError = SingleLiveEvent<Pair<String, Event>>()
    val eventCreatedError: LiveData<Pair<String, Event>>
        get() = _eventCreatedError
    private val _eventsRemoveError = SingleLiveEvent<Pair<String, Int>>()
    val eventsRemoveError: LiveData<Pair<String, Int>>
        get() = _eventsRemoveError
    private val _eventsLikeError = SingleLiveEvent<Pair<String, Pair<Int, Boolean>>>()
    val eventsLikeError: LiveData<Pair<String, Pair<Int, Boolean>>>
        get() = _eventsLikeError
    private val _usersLoadError = SingleLiveEvent<String>()
    val usersLoadError: LiveData<String>
        get() = _usersLoadError

    init {
        load()
        loadUsers()
    }

    fun load() = viewModelScope.launch {
        _dataState.value = FeedModelState.Loading
//        try {
            repository.getAll(appAuth.getToken())
            _dataState.value = FeedModelState.Idle
//        } catch (e: Exception) {
//            _dataState.value = FeedModelState.Error
//        }
    }

    fun loadUsers() = viewModelScope.launch {
        _dataState.value = FeedModelState.Loading
        try {
            usersRepository.getUsers()
            _dataState.value = FeedModelState.Idle
        } catch (e: Exception) {
            _usersLoadError.postValue(e.toString())
        }
    }

    fun likeById(id: Int, likedByMe: Boolean) = viewModelScope.launch {
        try {
            appAuth.getToken()?.let { repository.likeById(id, !likedByMe, it, appAuth.getId()) }
        } catch (e: Exception) {
            _eventsLikeError.postValue(e.toString() to (id to likedByMe))
        }
    }

    fun changeContent(
        content: String,
        link: String,
        coordsLat: String,
        coordsLong: String,
    ) {
        edited.value?.let {
            val text = content.trim()
            val textLink = link.trim()
            val textCoordsLat = coordsLat.trim()
            val textCoordsLong = coordsLong.trim()
            val coords = if (textCoordsLat.isNotBlank() && textCoordsLong.isNotBlank()) Coords(
                textCoordsLat,
                textCoordsLong
            ) else null
            edited.value =
                it.copy(
                    content = text,
                    link = if (textLink.isNotBlank()) textLink else null,
                    coords = coords,
                )
        }
    }

    fun changeMentionedList(participatedList: List<Int>) {
        edited.value?.let {
            edited.value = it.copy(
                participantsIds = participatedList,
                participatedByMe = participatedList.contains(appAuth.getId())
            )
        }
    }

    fun save() = viewModelScope.launch {
        edited.value?.let {
            appAuth.getToken()?.let { token ->
                try {
                    when (_attachment.value) {
                        noMedia -> repository.save(it, token)
                        else -> _attachment.value?.file?.let { file ->
                            repository.saveWithAttachment(
                                it,
                                file,
                                token,
                                _attachment.value!!.attachmentType
                            )
                        }
                    }
                    _eventCreated.postValue(Unit)
                    empty()
                } catch (e: Exception) {
                    println(e.message.toString())
                    _eventCreatedError.postValue(e.message.toString() to it)
                }
            }
        }
    }

    fun edit(event: Event) {
        edited.value = event
    }

    fun empty() {
        edited.value = emptyEvent
        deleteMedia()
    }

    fun removeById(id: Int) = viewModelScope.launch {
        try {
            appAuth.getToken()?.let { repository.removeById(it, id) }
        } catch (e: Exception) {
            println(e.message)
            _eventsRemoveError.postValue(e.message.toString() to id)
        }
    }

    fun changeMedia(fileUri: Uri?, toFile: File?, attachmentType: AttachmentType) {
        _attachment.value = MediaModel(fileUri, toFile, attachmentType)
    }

    fun deleteMedia() {
        _attachment.value = noMedia
    }

    fun getBackOldUsers(oldUserList: List<User>) = viewModelScope.launch {
        usersRepository.getBackOldUsers(oldUserList)
    }

    fun changeCheckedUsers(id: Int, changeToOtherState: Boolean) = viewModelScope.launch {
        usersRepository.changeCheckedUsers(id, changeToOtherState)
    }

    fun changeUserId(userId: Int) {
        appAuth.userId = userId
    }
}

private val emptyEvent = Event(
    id = 0,
    content = "",
    author = "Me",
    authorAvatar = null,
    published = "",
    datetime = "2023-04-16T07:12:10.712094Z",
    type = EventType.OFFLINE,
)
private val noMedia = MediaModel(null, null, AttachmentType.NONE)