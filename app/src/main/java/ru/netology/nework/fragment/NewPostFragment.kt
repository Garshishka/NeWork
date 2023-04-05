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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    val resultLauncher =
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
        val isEditing = arguments?.textArg != null

        if (isEditing) {
            arguments?.apply {
                textArg.let(binding.edit::setText)
                linkArg.let(binding.link::setText)
                latArg.let(binding.coordinatesLat::setText)
                longArg.let(binding.coordinatesLong::setText)
                if (mentionedArg != null) {
                    viewModel.userList =
                        Gson().fromJson(mentionedArg, object : TypeToken<List<Int>>() {}.type)
                } else {
                    viewModel.userList.clear()
                }
            }
            viewModel.apply {
                draft = ""
                draftCoordsLat = ""
                draftCoordsLong = ""
                draftLink = ""
            }
        } else {
            binding.apply {
                edit.setText(viewModel.draft)
                coordinatesLat.setText(viewModel.draftCoordsLat)
                coordinatesLong.setText(viewModel.draftCoordsLong)
                link.setText(viewModel.draftLink)
            }
        }
        binding.edit.requestFocus()

        subscribe(isEditing)

        return binding.root
    }

    private fun subscribe(isEditing: Boolean) {
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
                viewModel.apply {
                    draft = ""
                    draftCoordsLat = ""
                    draftCoordsLong = ""
                    draftLink = ""
                }
                val text = binding.edit.text.toString()
                val link = binding.link.text.toString()
                val coordsLat = binding.coordinatesLat.text.toString()
                val coordsLong = binding.coordinatesLong.text.toString()
                if ((coordsLat.isBlank() && coordsLong.isNotBlank()) || (coordsLat.isNotBlank() && coordsLong.isBlank())) {
                    Toast.makeText(
                        context,
                        getString(R.string.coords_not_both_filled),
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    if (text.isNotBlank()) {
                        viewModel.changeContent(text, link, coordsLat, coordsLong)
                        viewModel.save()
                        findNavController().navigateUp()
                    }
                }
                AndroidUtils.hideKeyboard(requireView())
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
            addMention.text = if(viewModel.userList.isNotEmpty()) viewModel.userList.size.toString() else ""
            addMention.setOnClickListener {
                findNavController().navigate(R.id.action_newPostFragment_to_usersFragment)
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
                viewModel.apply {
                    draft = binding.edit.text.toString()
                    draftLink = binding.link.text.toString()
                    draftCoordsLat = binding.coordinatesLat.text.toString()
                    draftCoordsLong = binding.coordinatesLong.text.toString()
                }
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
        var Bundle.linkArg by StringArg
        var Bundle.latArg by StringArg
        var Bundle.longArg by StringArg
        var Bundle.mentionedArg by StringArg
    }
}