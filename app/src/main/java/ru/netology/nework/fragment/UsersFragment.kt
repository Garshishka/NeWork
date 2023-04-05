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
import ru.netology.nework.utils.UserListInteractionListener
import ru.netology.nework.viewmodel.PostViewModel

class UsersFragment : Fragment() {
    private val viewModel: PostViewModel by  activityViewModels()

    private val userListInteractionListener = object : UserListInteractionListener{
        override fun onClick(id: Int) {
            if (viewModel.userList.contains(id)){
                viewModel.userList.remove(id)
            }
            else
            {
                viewModel.userList.add(id)
            }
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

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        viewModel.loadUsers()

        viewModel.usersData.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        return binding.root
    }

}
