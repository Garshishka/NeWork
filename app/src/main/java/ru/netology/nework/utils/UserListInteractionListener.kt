package ru.netology.nework.utils

import ru.netology.nework.dto.User

interface UserListInteractionListener {
    fun onClick(user: User)
}