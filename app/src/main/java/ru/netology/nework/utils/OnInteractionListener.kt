package ru.netology.nework.utils

import ru.netology.nework.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post)

    //    fun onShare(post: Post) {}
    fun onEdit(post: Post)
    fun onRemove(post: Post)

    //    fun onVideoClick(post: Post) {}
//    fun onPostClick(post: Post) {}
    fun onAudioClick(url: String)
    fun onVideoClick(url: String)
    fun onPictureClick(url: String)

}