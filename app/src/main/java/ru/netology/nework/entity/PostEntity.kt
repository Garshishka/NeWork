package ru.netology.nework.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nework.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val published: String,
    val coordinates: String = "no coords for now", //TODO(coordinates)
    val link: String?,
    //@TypeConverters(IdListConverter::class)
    val likeOwnerIds: List<Int>,
   // @TypeConverters(IdListConverter::class)
    val mentionIds: List<Int>,
    val mentionedMe: Boolean = false,
    val likedByMe: Boolean = false,
    @Embedded
    val attachment: AttachmentEmbedabble?,

    val notOnServer: Boolean = false,
    val show: Boolean = true,
){
    fun toDto() = Post(
        id,
        authorId,
        author,
        authorAvatar,
        authorJob,
        content,
        published,
        null,
        link,
        likeOwnerIds,
        mentionIds,
        mentionedMe,
        likedByMe,
        attachment?.toDto(),

    )

    companion object {
        fun fromDto(dto: Post, notOnServer: Boolean = false) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.authorJob,
                dto.content,
                dto.published,
                "coords here",
                dto.link,
                dto.likeOwnerIds,
                dto.mentionIds,
                dto.mentionedMe,
                dto.likedByMe,
                AttachmentEmbedabble.fromDto(dto.attachment),
            )
    }
}