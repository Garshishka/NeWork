package ru.netology.nework.auth

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.netology.nework.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

data class AuthPair(val id: Long, val token: String)

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val tokenKey = "TOKEN_KEY"
    private val idKey = "ID_KEY"
    private val _state: MutableStateFlow<AuthState?>

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0L)

        if (token == null || !prefs.contains(idKey)) {
            prefs.edit { clear() }
            _state = MutableStateFlow(null)
        } else {
            _state = MutableStateFlow(AuthState(id, token))
        }
        //sendPushToken()
    }

    val state = _state.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        prefs.edit {
            putLong(idKey, id)
            putString(tokenKey, token)
        }
        _state.value = AuthState(id, token)
      //  sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        prefs.edit { clear() }
        _state.value = null
       // sendPushToken()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService
    }

    /*fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val entryPoint =
                    EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
                entryPoint.getApiService().sendPushToken(
                    PushToken(
                        token ?: FirebaseMessaging.getInstance().token.await()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/
}