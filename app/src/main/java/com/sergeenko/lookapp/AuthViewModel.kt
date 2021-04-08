package com.sergeenko.lookapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.vk.api.sdk.auth.VKAccessToken
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
class AuthViewModel @ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(repository, savedStateHandle) {

    fun logWithVk(token: VKAccessToken) {
        logIn(token.accessToken.trim(), "vkontakte")
    }

    init {
        checkLogin()
    }

    private fun checkLogin(){
        viewModelScope.launch(IO) {
            val sr = repository.getDB().socialResponseDao().get()
            if(sr != null)
                modelState.emit(ModelState.Success(sr))
        }
    }

    fun logWithGoogle(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
                repository.getAccessToken(account.serverAuthCode!!)
                        .catch { modelState.emit(ModelState.Error(it.message)) }
                        .collect {
                            logIn(it.access_token, "google")
                        }

            } catch (e: ApiException) {
                //modelState.emit(ModelState.Error(e.localizedMessage))
            }
        }
    }

    fun logWithFacebook(loginResult: LoginResult?) {
        loginResult?.accessToken?.token?.let { logIn(it, "facebook") }
    }

    @ExperimentalCoroutinesApi
    private fun logIn(accessToken: String, provider: String) {
        if(modelState.value != ModelState.Loading) {
            viewModelScope.launch {
                doCollect(repository.logIn(accessToken = accessToken, provider = provider))
            }
        }
    }
}