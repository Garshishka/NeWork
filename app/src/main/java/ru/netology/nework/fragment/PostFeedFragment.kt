package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.adapter.PostsAdapter
import ru.netology.nework.databinding.FragmentPostsBinding
import ru.netology.nework.viewmodel.PostViewModel

@AndroidEntryPoint
class PostFeedFragment: Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    //private val authViewModel: AuthViewModel by activityViewModels()
    lateinit var binding : FragmentPostsBinding

    val adapter = PostsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater,container,false)

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