package ru.netology.nework.dto

import com.google.gson.annotations.SerializedName

data class Post (
    val id: Long,
    val authorId: Long = 0,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    val coords: Сoords?,
    val link: String?,
    val likeOwnerIds: List<Int>,
    val mentionIds: List<Int>,
    val mentionedMe: Boolean = false,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    @SerializedName("users")
    val users: Map<Long,UserPreview>,
)