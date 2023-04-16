package ru.netology.nework.repository

import androidx.lifecycle.LiveData
import ru.netology.nework.dto.User

interface UsersRepository {
    val usersData: LiveData<List<User>>
    suspend fun getUsers()
    suspend fun getBackOldUsers(oldUsers: List<User>)
    suspend fun changeCheckedUsers(id: Int, changeToOtherState: Boolean)
}