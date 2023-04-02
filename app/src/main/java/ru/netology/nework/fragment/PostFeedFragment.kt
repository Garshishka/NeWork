package ru.netology.nework.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.R
import ru.netology.nework.adapter.PostsAdapter
import ru.netology.nework.databinding.FragmentPostsBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.fragment.NewPostFragment.Companion.textArg
import ru.netology.nework.fragment.PictureFragment.Companion.urlArg
import ru.netology.nework.utils.OnInteractionListener
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.PostViewModel

@AndroidEntryPoint
class PostFeedFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    lateinit var binding: FragmentPostsBinding

    private val onInteractionListener = object : OnInteractionListener {
        override fun onLike(post: Post) {
            val token = context?.getSharedPreferences("auth", Context.MODE_PRIVATE)
                ?.getString("TOKEN_KEY", null)
            if (token == null) {
                context?.let { showSignInDialog(it) }
            } else {
                viewModel.likeById(post.id, post.likedByMe)
            }
        }

        override fun onEdit(post: Post) {
            findNavController().navigate(R.id.action_postFeedFragment_to_newPostFragment,
                Bundle().apply
                { textArg = post.content })
            viewModel.edit(post)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onAudioClick(url: String) {
            findNavController().navigate(R.id.action_postFeedFragment_to_audioFragment,
                Bundle().apply
                { urlArg = url })
        }

        override fun onVideoClick(url: String) {
            findNavController().navigate(R.id.action_postFeedFragment_to_playFragment,
                Bundle().apply
                { urlArg = url })
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

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swiper.isRefreshing =
                    it.refresh is LoadState.Loading
            }
        }

        authViewModel.state.observe(viewLifecycleOwner) {
            if (it?.id != -1) {
                adapter.refresh()
            }
        }

        binding.apply {
            swiper.setOnRefreshListener {
                adapter.refresh()
            }

            addPostButton.setOnClickListener {
                val token = context?.getSharedPreferences("auth", Context.MODE_PRIVATE)
                    ?.getString("TOKEN_KEY", null)
                if (token == null) {
                    context?.let { context -> showSignInDialog(context) }
                } else {
                    findNavController().navigate(R.id.action_postFeedFragment_to_newPostFragment)
                }
            }
        }

        viewModel.apply {
            postCreatedError.observe(viewLifecycleOwner) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.specific_posting_error, it),
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Retry post") {
                        viewModel.load()
                    }
                    .show()
            }

            postsRemoveError.observe(viewLifecycleOwner) {
                val id = it.second
                Snackbar.make(
                    binding.root,
                    getString(R.string.specific_edit_error, it.first),
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Retry") {
                        viewModel.removeById(id)
                    }
                    .show()
            }

            postsLikeError.observe(viewLifecycleOwner) {
                val id = it.second.first
                val willLike = it.second.second
                Snackbar.make(
                    binding.root,
                    getString(R.string.specific_edit_error, it.first),
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Retry") {
                        viewModel.likeById(id, willLike)
                    }
                    .show()
            }
        }
    }

    private fun showSignInDialog(context: Context) {
        val alertDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(context)
            builder.apply {
                setTitle(R.string.authorization_required)
                setMessage(getString(R.string.dialog_sign_in))
                setPositiveButton(
                    getString(R.string.sign_in)
                ) { _, _ ->
                    findNavController().navigate(R.id.action_global_fragment_sing_in)
                }
                setNeutralButton(
                    getString(R.string.back)
                ) {_, _ ->
                }
                setNegativeButton(
                    getString(R.string.sign_up)
                ) { _, _ ->
                    findNavController().navigate(R.id.action_global_signUpFragment)
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

}