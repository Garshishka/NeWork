package ru.netology.nework.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.R
import ru.netology.nework.adapter.PostsAdapter
import ru.netology.nework.databinding.FragmentPostsBinding
import ru.netology.nework.dto.FeedModelState
import ru.netology.nework.dto.Post
import ru.netology.nework.fragment.PictureFragment.Companion.urlArg
import ru.netology.nework.fragment.UserWallFragment.Companion.userIdArg
import ru.netology.nework.fragment.UserWallFragment.Companion.userJobArg
import ru.netology.nework.utils.PostInteractionListener
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.PostViewModel
import ru.netology.nework.viewmodel.UserWallViewModel

@AndroidEntryPoint
open class PostFeedFragment : Fragment() {
    protected open val viewModel: PostViewModel by activityViewModels()
    protected val authViewModel: AuthViewModel by activityViewModels()
    protected lateinit var binding: FragmentPostsBinding
    protected lateinit var postData: Flow<PagingData<Post>>

    protected val onInteractionListener = object : PostInteractionListener {
        override fun onLike(post: Post) {
            val token = authViewModel.state.value?.token
            if (token == null || token == "0") {
                context?.let { showSignInDialog(it) }
            } else {
                viewModel.likeById(post.id, post.likedByMe)
            }
        }

        override fun onEdit(post: Post) {
            viewModel.edit(post)
            findNavController().navigate(R.id.action_global_newPostFragment)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onAudioClick(url: String) {
            findNavController().navigate(R.id.action_global_audioFragment,
                Bundle().apply
                { urlArg = url })
        }

        override fun onVideoClick(url: String) {
            findNavController().navigate(R.id.action_global_playFragment,
                Bundle().apply
                { urlArg = url })
        }

        override fun onPictureClick(url: String) {
            findNavController().navigate(R.id.action_global_pictureFragment,
                Bundle().apply
                { urlArg = url })
        }

        override fun onAvatarClick(post: Post) {
            if(viewModel is UserWallViewModel){
                //in user wall avatar click does nothing
            } else {
                viewModel.changeUserId(post.authorId)
                findNavController().navigate(R.id.action_postFeedFragment_to_userWallFragment,
                    Bundle().apply
                    {
                        userIdArg = post.authorId.toString()
                        userJobArg = post.authorJob
                    })
            }
        }
    }

    protected val adapter = PostsAdapter(onInteractionListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        binding.listPosts.adapter = adapter
        postData = viewModel.data

        subscribe()
        subscribeForFeedWall()

        return binding.root
    }


    protected fun subscribe() {
        lifecycleScope.launchWhenCreated {
            postData.collectLatest {
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
            viewModel.load()
            if (it?.id != -1) {
                adapter.refresh()
            }
        }

        binding.apply {
            swiper.setOnRefreshListener {
                adapter.refresh()
            }

            jobsButton.setOnClickListener {
                val token = authViewModel.state.value?.token
                if (token == null || token == "0") {
                    context?.let { context -> showSignInDialog(context) }
                } else {
                    findNavController().navigate(R.id.action_global_jobFragment)
                }
            }

            addPostButton.setOnClickListener {
                val token = authViewModel.state.value?.token
                if (token == null || token == "0") {
                    context?.let { context -> showSignInDialog(context) }
                } else {
                    findNavController().navigate(R.id.action_global_newPostFragment)
                }
            }
        }

        viewModel.apply {
            dataState.observe(viewLifecycleOwner) {
                when (it) {
                    FeedModelState.Error -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.load_feed_error),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Retry") {
                                load()
                            }
                            .show()
                    }
                    else -> {}
                }
                binding.loading.isVisible = it == FeedModelState.Loading
            }

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

            usersLoadError.observe(viewLifecycleOwner) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.load_users_error, it),
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Retry") {
                        loadUsers()
                    }
                    .show()
            }
        }
    }

    private fun subscribeForFeedWall() {
        viewModel.apply {
            loadUsers()
        }

        binding.apply {
            myWallButton.setOnClickListener {
                val token = authViewModel.state.value?.token
                if (token == null || token == "0") {
                    context?.let { context -> showSignInDialog(context) }
                } else {
                    val id = authViewModel.state.value!!.id
                    viewModel.changeUserId(id)
                    findNavController().navigate(R.id.action_postFeedFragment_to_userWallFragment,
                        Bundle().apply
                        {
                            userIdArg = id.toString()
                            userJobArg = ""
                        })
                }
            }
        }
    }

    protected fun showSignInDialog(context: Context) {
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
                ) { _, _ ->
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