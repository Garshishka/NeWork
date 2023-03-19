package ru.netology.nework.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.FragmentSignInBinding
import ru.netology.nework.viewmodel.SignInViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : Fragment() {
    @Inject
    lateinit var appAuth: AppAuth

    lateinit var binding: FragmentSignInBinding
    private val viewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        subscribe()

        return binding.root
    }

    private fun subscribe() {
        binding.apply {
            //DEBUG
            showAuth()
           //DEBUG

            signUpButton.setOnClickListener {
                findNavController().navigate(R.id.action_fragment_sing_in_to_signUpFragment)
            }

            signInButton.setOnClickListener {
                if (binding.loginInput.text.isNullOrEmpty() || binding.passwordInput.text.isNullOrEmpty()) {
                    Toast.makeText(context, "Both fields need to be filled!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    viewModel.signIn(
                        binding.loginInput.text.toString(),
                        binding.passwordInput.text.toString()
                    )
                }
            }
        }

        viewModel.apply {
            signInRight.observe(viewLifecycleOwner) {
                appAuth.setAuth(it.id, it.token)
                //DEBUG
                Toast.makeText(context, "LOGIN SUCCESSFUL", Toast.LENGTH_LONG)
                    .show()
                showAuth()
                //DEBUG
                //TODO("Make navigation to main screen")
            }

            signInError.observe(viewLifecycleOwner) {
                Toast.makeText(context, getString(R.string.login_error, it), Toast.LENGTH_LONG)
                    .show()
            }

            signInWrong.observe(viewLifecycleOwner) {
                Toast.makeText(context, getString(R.string.login_wrong), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    //DEBUG
    private fun showAuth(){
        binding.debugId.text = appAuth.state.value?.id.toString()
        binding.debugToken.text = appAuth.state.value?.token.toString()
    }
}