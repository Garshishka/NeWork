package ru.netology.nework.utils

interface OnInteractionListener {
//    fun onLike(post: Post) {}
//    fun onShare(post: Post) {}
//    fun onEdit(post: Post) {}
//    fun onRemove(post: Post) {}
//    fun onVideoClick(post: Post) {}
//    fun onPostClick(post: Post) {}
    fun onAttachmentPlayClick(url: String, isVideo: Boolean)
    fun onPictureClick(url: String) {}
}