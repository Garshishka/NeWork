package ru.netology.nework.dto

import com.google.gson.annotations.SerializedName

data class Post (
    val id: Int,
    val authorId: Int = 0,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    val coords: Сoords? = null,
    val link: String? = null,
    val likeOwnerIds: List<Int> = emptyList(),
    val mentionIds: List<Int> = emptyList(),
    val mentionedMe: Boolean = false,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    @SerializedName("users")
    val users: Map<Int,UserPreview> = emptyMap(),
)