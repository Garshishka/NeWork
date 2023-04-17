package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nework.adapter.UsersAdapter
import ru.netology.nework.databinding.FragmentUsersBinding
import ru.netology.nework.dto.User
import ru.netology.nework.utils.listeners.UserListInteractionListener
import ru.netology.nework.viewmodel.PostViewModel

class UsersFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()

    val mentionedList = mutableListOf<Int>()

    private val userListInteractionListener = object : UserListInteractionListener {
        override fun onClick(user: User) {
            if (mentionedList.contains(user.id))
                mentionedList.remove(user.id)
            else
                mentionedList.add(user.id)
            viewModel.changeCheckedUsers(user.id, true)
        }
    }

    private val adapter = UsersAdapter(userListInteractionListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val oldUserList = viewModel.usersData.value?.toList()
        val binding = FragmentUsersBinding.inflate(inflater, container, false)
        //For users already in mention list we check them
        viewModel.edited.value?.mentionIds?.forEach {
            viewModel.changeCheckedUsers(it, false)
            mentionedList.add(it)
        }
        binding.usersList.adapter = adapter

        binding.addUsersButton.setOnClickListener {
            viewModel.changeMentionedList(mentionedList)
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (oldUserList != null) {
                viewModel.getBackOldUsers(oldUserList)
            }
            findNavController().navigateUp()
        }

        viewModel.usersData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

}
