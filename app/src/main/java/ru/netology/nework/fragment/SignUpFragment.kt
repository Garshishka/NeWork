package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.FragmentSignUpBinding
import ru.netology.nework.viewmodel.SignUpViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    @Inject
    lateinit var appAuth: AppAuth

    lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        subscribe()

        return binding.root
    }

    private fun subscribe() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            goBack()
        }

        binding.apply {
            signUpButton.setOnClickListener {
                if (binding.loginInput.text.isNullOrEmpty()
                    || binding.passwordInput.text.isNullOrEmpty()
                    || binding.repeatPasswordInput.text.isNullOrEmpty()
                    || binding.usernameInput.text.isNullOrEmpty()
                ) {
                    Toast.makeText(context, "All fields need to be filled!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    if (binding.passwordInput.text.toString() != binding.repeatPasswordInput.text.toString()) {
                        Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        viewModel.signUp(
                            binding.loginInput.text.toString(),
                            binding.passwordInput.text.toString(),
                            binding.usernameInput.text.toString()
                        )
                    }
                }
            }
        }

        viewModel.apply {
            signUpRight.observe(viewLifecycleOwner) {
                appAuth.setAuth(it.id, it.token)
                //DEBUG
                Toast.makeText(context, "LOGIN SUCCESSFUL", Toast.LENGTH_LONG)
                    .show()
                //DEBUG
                goBack()
            }

            signUpError.observe(viewLifecycleOwner) {
                Toast.makeText(context, getString(R.string.login_error, it), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun goBack() {
        findNavController().navigateUp()
    }


}