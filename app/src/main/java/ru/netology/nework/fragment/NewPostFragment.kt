package ru.netology.nework.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
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
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.utils.StringArg
import ru.netology.nework.viewmodel.PostViewModel
import javax.inject.Inject

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: PostViewModel by activityViewModels()

    lateinit var binding: FragmentNewPostBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(inflater, container, false)
        val isEditing = arguments?.textArg != null

        if (isEditing) {
            arguments?.textArg.let(binding.edit::setText)
            viewModel.draft = ""
        } else {
            binding.edit.setText(viewModel.draft)
        }
        binding.edit.requestFocus()

        subscribe(isEditing)

        return binding.root
    }

    private fun subscribe(isEditing: Boolean) {
        val pickImageContract =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.changeMedia(uri,
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
                    viewModel.changeMedia(uri,
                        context?.let { AndroidUtils.fileFromContentUri(it, uri) },
                        AttachmentType.VIDEO
                    )
                } else {
                    println("No media selected")
                }
            }

        binding.apply {
            sendButton.setOnClickListener {
                viewModel.draft = ""
                val text = binding.edit.text.toString()
                if (text.isNotBlank()) {
                    viewModel.changeContent(text)
                    viewModel.save()
                    findNavController().navigateUp()
                }
                AndroidUtils.hideKeyboard(requireView())
            }
            pickPhoto.setOnClickListener {
                pickImageContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            pickVideo.setOnClickListener {
                pickVideoContract.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            }
            removeAttachment.setOnClickListener {
                viewModel.deleteMedia()
            }
        }

        viewModel.attachment.observe(viewLifecycleOwner) {
            binding.apply {
                when (it.attachmentType) {
                    AttachmentType.IMAGE -> photo.setImageURI(it.uri)
                    AttachmentType.VIDEO -> photo.setImageResource(R.drawable.baseline_video_48)
                    AttachmentType.AUDIO -> photo.setImageResource(R.drawable.baseline_audio_file_48)
                    else -> {}
                }
                photoContainer.isVisible = it.uri != null
            }
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
            if (!isEditing) {
                viewModel.draft = binding.edit.text.toString()
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

    companion object {
        var Bundle.textArg by StringArg
    }
}