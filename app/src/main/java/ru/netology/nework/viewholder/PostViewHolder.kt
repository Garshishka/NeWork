package ru.netology.nework.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.LayoutPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Post

class PostViewHolder(
    private val binding: LayoutPostBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post){
        binding.apply {
            //wholePost.setOnClickListener { onInteractionListener.onPostClick(post) }
            author.text = post.author
            //val publishedTime = Timestamp(post.published.toLong()*1_000)
            published.text = post.published//Date(publishedTime.time).toString()
            content.text = post.content
            like.text = formattingBigNumbers(post.likesAmount)
            like.isChecked = post.likedByMe
            //like.setOnClickListener { onInteractionListener.onLike(post) }
            mention.text = formattingBigNumbers(post.mentionsAmount)
           //mention.setOnClickListener { onInteractionListener.onShare(post) }
            //viewsText.text = formattingBigNumbers(post.views)

            if (post.attachment != null) {
                attachmentPicture.visibility = View.VISIBLE
               // val attachmentUrl = "${BuildConfig.BASE_URL}/media/${post.attachment.url}"
               // binding.attachmentPicture.load(attachmentUrl)
                if (post.attachment.type == AttachmentType.IMAGE) {
                    //attachmentPicture.setOnClickListener { onInteractionListener.onPictureClick(post.attachment.url) }
                    playButton.visibility = View.GONE
                } else {
                    //attachmentPicture.setOnClickListener { onInteractionListener.onVideoClick(post) }
                }
            } else attachmentPicture.visibility = View.GONE

            if (post.author == "Me") {
                notOnServer.visibility = View.VISIBLE
                bottomGroup.visibility = View.GONE
            } else {
                notOnServer.visibility = View.GONE
                bottomGroup.visibility = View.VISIBLE
            }

            /* TODO(POST MENUS)
            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            val avatarUrl = "${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}"
            binding.avatar.load(avatarUrl, true)

             */
        }
    }


    private fun formattingBigNumbers(number: Long): String {
        return when (number) {
            in 0..999 -> number.toString()
            in 1000..1099 -> "1k"
            in 1100..9_999 -> (number.toDouble() / 1000).toString().take(3) + "K"
            in 10_000..99_999 -> (number.toDouble() / 1000).toString().take(2) + "K"
            in 100_000..999_999 -> (number.toDouble() / 1000).toString().take(3) + "K"
            else -> {
                val mNumber = (number.toDouble() / 1_000_000).toString()
                val strings = mNumber.split(".")
                strings[0] + "." + strings[1].take(1) + "M"
            }
        }
    }
}