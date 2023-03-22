package ru.netology.nework.dto

data class Post (
    val id: Long,
    val authorId: Long = 0,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    val coordinates: Coordinates?,
    val link: String?,
    val likeOwnerIds: List<Int>,
    //TODO(like and mention list)
    val mentionIds: List<Int>,
    val mentionedMe: Boolean = false,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    //TODO(user list)
)