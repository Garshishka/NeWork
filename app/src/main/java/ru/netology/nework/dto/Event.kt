package ru.netology.nework.dto

import com.google.gson.annotations.SerializedName

enum class EventType {
    OFFLINE, ONLINE
}

data class Event (
    val id: Int,
    val authorId: Int = 0,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coords? = null,
    val type: EventType,
    val likeOwnerIds: List<Int> = emptyList(),
    val likedByMe: Boolean = false,
    val speakersIds: List<Int> = emptyList(),
    val participantIds: List<Int> = emptyList(),
    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
    @SerializedName("users")
    val users: Map<Int,UserPreview> = emptyMap(),
)