package ru.netology.nework.dto

data class Attachment(
    val url: String,
    //val description: String,
    val type: AttachmentType = AttachmentType.IMAGE
)

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO,
}
