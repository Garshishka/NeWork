package ru.netology.nework.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.databinding.LayoutPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.Post
import ru.netology.nework.dto.UserPreview
import ru.netology.nework.utils.OnInteractionListener
import ru.netology.nework.utils.load
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class PostViewHolder(
    private val binding: LayoutPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            //wholePost.setOnClickListener { onInteractionListener.onPostClick(post) }
            author.text = post.author
            if (post.authorAvatar != null) {
                avatar.load(post.authorAvatar, true)
            } else {
                avatar.setImageResource(R.drawable.baseline_person_24)
            }
            try {
                val publishedTime = OffsetDateTime.parse(post.published).toLocalDateTime()
                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd")
                published.text = publishedTime.format(formatter)
            } catch (e: Exception){
                println(e.message)
                published.text = "now"
            }
            content.text = post.content
            ifHaveTextThenShow(link, post.link)
            ifHaveTextThenShow(coordinates, post.coords)

            like.text = formattingBigNumbers(post.likeOwnerIds.size.toLong())
            like.isChecked = post.likedByMe
            like.setOnClickListener { onInteractionListener.onLike(post) }

            showAvatarInTrailing(post.likeOwnerIds,0,likeAvatars1,post.users)
            showAvatarInTrailing(post.likeOwnerIds,1,likeAvatars2,post.users)
            showAvatarInTrailing(post.likeOwnerIds,2,likeAvatars3,post.users)
            likeBatchTrail.isVisible = post.likeOwnerIds.size.toLong() > 3

            mention.text = formattingBigNumbers(post.mentionIds.size.toLong())
            //mention.setOnClickListener { onInteractionListener.onShare(post) }
            showAvatarInTrailing(post.mentionIds,0,mentionAvatars1,post.users)
            showAvatarInTrailing(post.mentionIds,1,mentionAvatars2,post.users)
            showAvatarInTrailing(post.mentionIds,2,mentionAvatars3,post.users)
            mentionBatchTrail.isVisible = post.mentionIds.size.toLong() > 3

            if (post.attachment != null) {
                val attachmentUrl = post.attachment.url
                when (post.attachment.type) {
                    AttachmentType.IMAGE -> {
                        attachmentPicture.visibility = View.VISIBLE
                        playAttachment.visibility = View.GONE
                        attachmentPicture.load(attachmentUrl)
                        attachmentPicture.setOnClickListener {
                            onInteractionListener.onPictureClick(attachmentUrl)
                        }
                    }
                    AttachmentType.VIDEO -> {
                        attachmentPicture.visibility = View.GONE
                        playAttachment.visibility = View.VISIBLE
                        playAttachment.setImageResource(R.drawable.baseline_video_48)
                        playAttachment.setOnClickListener {
                            onInteractionListener.onVideoClick(attachmentUrl)
                        }
                    }
                    AttachmentType.AUDIO -> {
                        attachmentPicture.visibility = View.GONE
                        playAttachment.visibility = View.VISIBLE
                        playAttachment.setImageResource(R.drawable.baseline_audio_file_48)
                        playAttachment.setOnClickListener {
                            onInteractionListener.onAudioClick(attachmentUrl)
                        }
                    }
                    else -> {}
                }
            } else {
                attachmentPicture.visibility = View.GONE
                playAttachment.visibility = View.GONE
            }

            if (post.author == "Me") {
                notOnServer.visibility = View.VISIBLE
                bottomGroup.visibility = View.GONE
            } else {
                notOnServer.visibility = View.GONE
                bottomGroup.visibility = View.VISIBLE
            }

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
        }
    }

    private fun showAvatarInTrailing(
        avatarUserIdList: List<Int>,
        num: Int,
        avatar: ImageView,
        users: Map<Long, UserPreview>
    ) {
        if (avatarUserIdList.size > num) {
            val userId = avatarUserIdList[num].toLong()
            val user = users[userId]
            avatar.isVisible = true
            if (user != null && user.avatar != null) {
                avatar.load(user.avatar, true)
            } else {
                avatar.setImageResource(R.drawable.baseline_avatar_circle_filled_24)
            }
        } else {
            avatar.isVisible = false
        }
    }


    private fun <T> ifHaveTextThenShow(view: TextView, param: T?) {
        if (param != null) {
            view.isVisible = true
            view.text = param.toString()
        } else {
            view.isVisible = false
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