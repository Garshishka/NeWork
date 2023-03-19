package ru.netology.nework.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nework.api.ApiService
import ru.netology.nework.auth.AuthPair
import ru.netology.nework.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel(){
    private val _signInError = SingleLiveEvent<String>()
    val signInError: LiveData<String>
        get() = _signInError
    private val _signInWrong = SingleLiveEvent<Unit>()
    val signInWrong: LiveData<Unit>
        get() = _signInWrong
    private val _signInRight = SingleLiveEvent<AuthPair>()
    val signInRight: LiveData<AuthPair>
        get() = _signInRight

    fun signIn(login: String, password: String)  = viewModelScope.launch {
        try {
            val response = apiService.updateUser(login, password)
            if (!response.isSuccessful) {
                if (response.code()==400||response.code() == 404){
                    _signInWrong.postValue(Unit)
                }
                _signInError.postValue(response.message().toString())//response.code().toString())
            }
            val authPair = response.body() ?: throw RuntimeException("body is null")
            _signInRight.postValue(authPair)
        } catch (e: Exception) {
            _signInError.postValue(e.toString())
        }
    }

}