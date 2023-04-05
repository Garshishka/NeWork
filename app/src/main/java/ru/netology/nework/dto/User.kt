package ru.netology.nework.dto

data class User(
    val id : Int,
    val login: String,
    val name: String,
    val avatar: String?,
    val checkedNow: Boolean = false
)
