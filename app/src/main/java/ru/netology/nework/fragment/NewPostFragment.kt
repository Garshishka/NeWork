package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.FragmentNewPostBinding
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

        subscribe()

        return binding.root
    }

    private fun subscribe() {
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
        }
    }

    companion object {
        var Bundle.textArg by StringArg
    }
}