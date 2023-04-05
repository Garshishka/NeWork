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
import ru.netology.nework.utils.UserListInteractionListener
import ru.netology.nework.viewmodel.PostViewModel

class UsersFragment : Fragment() {
    private val viewModel: PostViewModel by  activityViewModels()

    private val userListInteractionListener = object : UserListInteractionListener{
        override fun onClick(user: User) {
            if (viewModel.userList.contains(user.id)){
                viewModel.userList.remove(user.id)
            }
            else{
                viewModel.userList.add(user.id)
            }
            viewModel.changeCheckedUsers(user.id)
        }

    }

    private val adapter = UsersAdapter(userListInteractionListener)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(inflater, container, false)
        binding.usersList.adapter = adapter

        binding.addUsersButton.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        viewModel.usersData.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        return binding.root
    }

}
