package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentPostsBinding
import ru.netology.nework.viewmodel.UserWallViewModel

@AndroidEntryPoint
class UserWallFragment : PostFeedFragment() {
    override val viewModel: UserWallViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)

        binding.listPosts.adapter = adapter
        postData = viewModel.dataMyWall

        subscribe()
        subscribeForUserWall()

        return binding.root
    }

    private fun subscribeForUserWall() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        binding.apply {
            feedButton.isVisible = true
            feedButton.setOnClickListener {
                findNavController().navigate(R.id.action_global_postFeedFragment)
            }

//            userInfo.isVisible = true
//            lifecycleScope.launchWhenCreated {
//                viewModel.getMyJob()
//                val job = viewModel.myJob
//                bindUserInfo(196,job) //TODO change user ID to real user ID
//            }
        }
    }


}