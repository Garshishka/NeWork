package ru.netology.nework.utils

import ru.netology.nework.dto.Post

interface PostInteractionListener {
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onAudioClick(url: String)
    fun onVideoClick(url: String)
    fun onPictureClick(url: String)

}