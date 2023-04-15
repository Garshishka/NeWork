package ru.netology.nework.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.dao.EventDao
import ru.netology.nework.dao.EventRemoteKeyDao
import ru.netology.nework.db.AppDb
import ru.netology.nework.dto.*
import ru.netology.nework.entity.EventEntity
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService,
    eventRemoteKeyDao: EventRemoteKeyDao,
    appDb: AppDb,
    private val auth: AppAuth,
) : EventRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { eventDao.getPagingSource() },
        remoteMediator = EventRemoteMediator(apiService, eventDao, eventRemoteKeyDao, appDb),
    ).flow
        .map { it.map(EventEntity::toDto) }

    private val emptyUsers: List<User> = emptyList()

    private val _usersData = MutableLiveData(emptyUsers)
    override val usersData: LiveData<List<User>>
        get() = _usersData

    //EVENTS
    override suspend fun getAll(authToken: String?) {
        val response = apiService.getAllEvents()
        if (!response.isSuccessful) {
            throw RuntimeException(response.code().toString())
        }
        val events = response.body() ?: throw RuntimeException("body is null")
        eventDao.insert(events.map(EventEntity.Companion::fromDto))

        if (authToken != null) {
            eventDao.getAllUnsent().forEach { save(it.toDto(), authToken) }
        }
    }

    override suspend fun removeById(authToken: String, id: Int) {
        val removed = eventDao.getById(id)
        eventDao.removeById(id)
        try {
            val response = apiService.removeById(authToken, id)
            if (!response.isSuccessful) {
                eventDao.insert(removed)
                throw RuntimeException(response.code().toString())
            }
        } catch (e: Exception) {
            eventDao.insert(removed)
            throw RuntimeException(e)
        }
    }

    override suspend fun save(event: Event, authToken: String) {
  /*      val mentionList =
            event.mentionIds.toList() //Hacky method, but for some reason ID conversion eats list
        eventDao.save(EventEntity.fromDto(event, true))
        try {
            val response = apiService.save(authToken), event.copy(mentionIds = mentionList))
            if (!response.isSuccessful) {
                throw RuntimeException(
                    response.code().toString()
                )
            }
            val body = response.body() ?: throw RuntimeException("body is null")
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }*/
    }

    override suspend fun saveWithAttachment(
        event: Event,
        file: File,
        authToken: String,
        attachmentType: AttachmentType
    ) {
        /*  try {
              val upload = upload(file, authToken)
              val eventWithAttachment =
                  event.copy(attachment = Attachment(upload.url, attachmentType))
              save(eventWithAttachment, authToken)
          } catch (e: Exception) {
              throw RuntimeException(e)
          }*/
    }

    override suspend fun likeById(
        id: Int,
        willLike: Boolean,
        authToken: String,
        userId: Int
    ): Event {
       /* eventDao.likeById(id, userId)
        try {
            val response = if (willLike)
                apiService.likeById(authToken, id)
            else
                apiService.dislikeById(authToken, id)
            if (!response.isSuccessful) {
                eventDao.likeById(id, userId)
                throw RuntimeException(response.code().toString())
            }
            return response.body() ?: throw RuntimeException("body is null")
        } catch (e: Exception) {
            eventDao.likeById(id, userId)
            throw RuntimeException(e)
     } */
        return Event(0,0,"",null,null,"","","", Coords("",""),EventType.OFFLINE, emptyList(),false,
            emptyList(), emptyList(),false,null,null,false, emptyMap()
        )
    }

    //MEDIA
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

    //USERS
    override suspend fun getUsers() {
        try {
            val response = apiService.getUsers()
            if (!response.isSuccessful) {
                throw RuntimeException(response.code().toString())
            }
            val users = response.body() ?: throw RuntimeException("body is null")
            _usersData.postValue(users)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    override suspend fun getBackOldUsers(oldUsers: List<User>) {
        _usersData.postValue(oldUsers)
    }

    override suspend fun changeCheckedUsers(id: Int, changeToOtherState: Boolean) {
        val data = _usersData.value
        val foundUser = data?.find { it.id == id }
        foundUser?.let {
            if (changeToOtherState) {
                it.checkedNow = !it.checkedNow
            } else {
                it.checkedNow = true
            }
            _usersData.postValue(data)
        }
    }
}