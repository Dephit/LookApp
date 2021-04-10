package com.sergeenko.lookapp.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sergeenko.lookapp.models.ModelState
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.interfaces.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class RegisterLoginViewModel @ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(repository, savedStateHandle) {

    var email: String = ""
    private val errorFlow: MutableStateFlow<String> = MutableStateFlow("")

    init {
        viewModelScope.launch {
            errorFlow
                .debounce(500)
                .collect { login ->
                    if(login.isNotEmpty()) {
                        repository.checkUsername(username = login)
                                .catch { modelState.emit(ModelState.Error(R.string.login_is_taken)) }
                                .collect {
                                    if (login.isNotEmpty()) {
                                        email = login
                                        modelState.emit(ModelState.Success(it))
                                    }
                                }
                    }
                }
        }
    }


    @ExperimentalCoroutinesApi
    fun checkNickName(login: String) {
        viewModelScope.launch {
            if(login.isEmpty())
                return@launch
            if(!login.matches(Regex("^[a-zA-Z0-9-_]+$"))) {
                modelState.emit(ModelState.Error(R.string.only_latin))
                return@launch
            }
            if(modelState.value !is ModelState.Loading)
                modelState.emit(ModelState.Loading)
            errorFlow.emit(login)
        }
    }

}