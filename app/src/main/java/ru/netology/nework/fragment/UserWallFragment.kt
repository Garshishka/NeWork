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
import ru.netology.nework.dto.User
import ru.netology.nework.fragment.JobFragment.Companion.idArg
import ru.netology.nework.utils.StringArg
import ru.netology.nework.utils.load
import ru.netology.nework.viewmodel.UserWallViewModel

@AndroidEntryPoint
class UserWallFragment : PostFeedFragment() {
    override val viewModel: UserWallViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userId: Int = arguments?.userIdArg?.let { arguments?.userIdArg?.toInt() } ?: 0
        val userJob = arguments?.userJobArg.toString()

        binding = FragmentPostsBinding.inflate(inflater, container, false)

        binding.listPosts.adapter = adapter
        postData = viewModel.data

        subscribe()
        subscribeForUserWall(userId, userJob)

        return binding.root
    }

    private fun subscribeForUserWall(userId: Int, job: String) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        binding.apply {
            feedButton.isVisible = true
            feedButton.setOnClickListener {
                findNavController().navigate(R.id.action_global_postFeedFragment)
            }

            userInfo.isVisible = true
            bindUserInfo(userId, job)
        }
    }

    private fun bindUserInfo(userId: Int, job: String) {
        val user: User? = viewModel.usersData.value?.find { user -> user.id == userId }
        user?.let { user ->
            binding.apply {
                userName.text = user.name
                if (userId == authViewModel.state.value?.id) {
                    //If its owner job we need to get it other way than from argument
                    //because if just click on a button - we can't get a job from a post
                    viewModel.getMyJob()
                    viewModel.myJob.observe(viewLifecycleOwner) {
                        userJob.text = it
                        userJob.isVisible = it != "" && it !="null"
                    }
                } else {
                    userJob.text = job
                    userJob.isVisible = job != "" && job !="null"
                }
                userJob.setOnClickListener {
                    findNavController().navigate(R.id.action_global_jobFragment,
                        Bundle().apply
                        { idArg = userId.toString() })
                }
                user.avatar?.let {
                    userAvatar.load(user.avatar, true)
                } ?: userAvatar.setImageResource(R.drawable.baseline_person_24)
            }
        }
    }

    companion object {
        var Bundle.userIdArg by StringArg
        var Bundle.userJobArg by StringArg
    }
}