package ru.netology.nework.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.FragmentNewPostBinding
import ru.netology.nework.dto.AttachmentType
import ru.netology.nework.dto.MediaModel
import ru.netology.nework.fragment.secondary.MapFragment.Companion.editingArg
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.utils.load
import ru.netology.nework.viewmodel.PostViewModel
import ru.netology.nework.viewmodel.UsersAndMapViewModel
import javax.inject.Inject

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: PostViewModel by activityViewModels()
    private val usersAndMapViewModel: UsersAndMapViewModel by activityViewModels()

    lateinit var binding: FragmentNewPostBinding

    //For choosing Audio file. There is an standard Image-Video picker but no Audio picker so it on its own
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                if (uri != null) {
                    viewModel.changeMedia(
                        uri,
                        context?.let { AndroidUtils.fileFromContentUri(it, uri) },
                        AttachmentType.AUDIO
                    )
                } else {
                    println("No media selected")
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        bind()
        binding.edit.requestFocus()

        subscribe()

        return binding.root
    }

    private fun bind() {
        if (usersAndMapViewModel.coords != null) {
            //we check if we have saved coordinates from map fragment
            //if we have - we save them to our edited/draft and clear
            viewModel.changeCoords(usersAndMapViewModel.coords!!)
            usersAndMapViewModel.coords = null
        }
        //edited is used as container for post info if we editing and as a draft saver for new post
        val post = viewModel.edited.value
        post?.mentionIds?.let { usersAndMapViewModel.getUserList(it) }
        binding.apply {
            edit.setText(post?.content)
            link.setText(post?.link)
            if (post?.coords != null) {
                coordinatesButton.text = "${post.coords.lat.take(9)} | ${post.coords.long.take(9)}"
                deleteCoordsButton.isVisible = true
                deleteCoordsButton.setOnClickListener {
                    deleteCoordsButton.isVisible = false
                    viewModel.changeCoords(null)
                    coordinatesButton.text = getText(R.string.coordinates_hint)
                }
            }
            if (post?.id != 0) { //If post ID is not 0 - we editing so there can be some attachment
                if (post?.attachment != null) {
                    when (post.attachment.type) {
                        AttachmentType.IMAGE -> photo.load(post.attachment.url)
                        AttachmentType.VIDEO -> photo.setImageResource(R.drawable.baseline_video_48)
                        AttachmentType.AUDIO -> photo.setImageResource(R.drawable.baseline_audio_file_48)
                        else -> {}
                    } //save the url so changeMedia function doesn't load again
                    viewModel.changeMedia(null, null, post.attachment.type, post.attachment.url)
                    photoContainer.isVisible = true
                } else {
                    viewModel.deleteMedia()
                    photoContainer.isVisible = false
                }
            } else { //If post ID is 0 - it's a new post but some attachment can be as a draft
                viewModel.attachment.value?.let { showAttachment(it) }
            }
        }
    }

    private fun subscribe() {
        val pickImageContract =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.changeMedia(
                        uri,
                        context?.let { AndroidUtils.fileFromContentUri(it, uri) },
                        AttachmentType.IMAGE
                    )
                } else {
                    println("No media selected")
                }
            }
        val pickVideoContract =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.changeMedia(
                        uri,
                        context?.let { AndroidUtils.fileFromContentUri(it, uri) },
                        AttachmentType.VIDEO
                    )
                } else {
                    println("No media selected")
                }
            }

        binding.apply {
            sendButton.setOnClickListener {
                val text = binding.edit.text.toString()
                val link = binding.link.text.toString()
                if (text.isNotBlank()) {
                    viewModel.changeContent(
                        text,
                        link,
                    )
                    viewModel.save()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.no_context),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
                AndroidUtils.hideKeyboard(requireView())
            }

            coordinatesButton.setOnClickListener {
                usersAndMapViewModel.coords = viewModel.edited.value?.coords
                findNavController().navigate(R.id.action_global_mapFragment,
                    Bundle().apply
                    {
                        editingArg = true
                    })
            }

            pickPhoto.setOnClickListener {
                pickImageContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            pickVideo.setOnClickListener {
                pickVideoContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
            pickAudio.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "audio/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                resultLauncher.launch(intent)
            }
            removeAttachment.setOnClickListener {
                viewModel.deleteMedia()
            }
            addMention.setOnClickListener {
                findNavController().navigate(R.id.action_newPostFragment_to_usersFragment)
            }
        }

        viewModel.attachment.observe(viewLifecycleOwner) {
            showAttachment(it)
        }

        usersAndMapViewModel.userIdList.observe(viewLifecycleOwner) {
            binding.addMention.text = if (it.isEmpty()) "" else it.size.toString()
            viewModel.changeMentionedList(it)
        }

        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logOut -> {
                        context?.let { showLogOutDialog(it) }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            //ID = 0 if new post (even with draft), non-zero will be edited posts
            if (viewModel.edited.value?.id == 0) {
                val text = binding.edit.text.toString()
                val link = binding.link.text.toString()
                viewModel.changeContent(
                    text,
                    link,
                )
            } else {
                viewModel.empty()
                viewModel.deleteMedia()
            }

            findNavController().navigateUp()
        }
    }

    private fun showLogOutDialog(context: Context) {
        val alertDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(context)
            builder.apply {
                setTitle(R.string.logging_out)
                setMessage(getString(R.string.dialog_log_out))
                setPositiveButton(
                    getString(R.string.log_out)
                ) { _, _ ->
                    appAuth.removeAuth()
                    findNavController().navigateUp()
                }
                setNegativeButton(
                    getString(R.string.back)
                ) { _, _ ->
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    private fun showAttachment(mediaModel: MediaModel) {
        binding.apply {
            when (mediaModel.attachmentType) {
                AttachmentType.IMAGE -> {
                    if (mediaModel.url == null) { //if we have url - then the image is already loaded
                        photo.setImageURI(mediaModel.uri)
                    }
                }
                AttachmentType.VIDEO -> photo.setImageResource(R.drawable.baseline_video_48)
                AttachmentType.AUDIO -> photo.setImageResource(R.drawable.baseline_audio_file_48)
                else -> {}
            }
            photoContainer.isVisible = mediaModel.attachmentType != AttachmentType.NONE
        }
    }

}