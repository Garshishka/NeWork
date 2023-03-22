package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.R
import ru.netology.nework.adapter.PostsAdapter
import ru.netology.nework.databinding.FragmentPostsBinding
import ru.netology.nework.fragment.PictureFragment.Companion.urlArg
import ru.netology.nework.fragment.PlayFragment.Companion.isVideoArg
import ru.netology.nework.utils.OnInteractionListener
import ru.netology.nework.viewmodel.PostViewModel

@AndroidEntryPoint
class PostFeedFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()

    //private val authViewModel: AuthViewModel by activityViewModels()
    lateinit var binding: FragmentPostsBinding

    private val onInteractionListener = object : OnInteractionListener {
        override fun onAttachmentPlayClick(url: String, isVideo: Boolean) {
            findNavController().navigate(
                R.id.action_postFeedFragment_to_playFragment,
                Bundle().apply
                {
                    urlArg = url
                    isVideoArg = isVideo
                })
        }

        override fun onPictureClick(url: String) {
            findNavController().navigate(R.id.action_postFeedFragment_to_pictureFragment,
                Bundle().apply
                { urlArg = url })
        }
    }

    val adapter = PostsAdapter(onInteractionListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        binding.listPosts.adapter = adapter

        subscribe()

        return binding.root
    }

    private fun subscribe() {

        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

    }
}