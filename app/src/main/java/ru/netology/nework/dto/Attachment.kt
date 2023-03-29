package ru.netology.nework.dto

import android.net.Uri
import java.io.File

data class PhotoModel(val uri: Uri?, val file: File?)

data class MediaUpload(val url: String)

data class Attachment(
    val url: String,
    val type: AttachmentType = AttachmentType.IMAGE
)

enum class AttachmentType {
    IMAGE,
    VIDEO,
    AUDIO,
}
