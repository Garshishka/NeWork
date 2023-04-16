package ru.netology.nework.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nework.api.ApiService
import ru.netology.nework.dto.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : UsersRepository {

    private val emptyUsers: List<User> = emptyList()

    private val _usersData = MutableLiveData(emptyUsers)
    override val usersData: LiveData<List<User>>
        get() = _usersData

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