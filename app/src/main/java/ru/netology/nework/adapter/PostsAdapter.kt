package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import ru.netology.nework.databinding.LayoutPostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.viewholder.PostDiffCallBack
import ru.netology.nework.viewholder.PostViewHolder

class PostsAdapter(
    //TODO(Interaction listener)
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallBack()) {
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = LayoutPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

}