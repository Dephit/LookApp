package com.sergeenko.lookapp.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sergeenko.lookapp.models.ModelState
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.interfaces.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class PhoneCodeEnterViewModel @ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(repository, savedStateHandle) {

    @ExperimentalCoroutinesApi
    fun sendCodeAgain(phone: String?) {
        viewModelScope.launch {
            if(modelState.value != ModelState.Loading) {
                if (phone != null) {
                    doCollect(repository.authByPhone(phone))
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun checkCode(code: String,phone: String) {
        if(modelState.value !is ModelState.Loading) {
            if(code.length < 5)
                return
            viewModelScope.launch {
                repository.checkCode(code = code, phone = phone).onStart { modelState.emit(ModelState.Loading)}
                        .catch { modelState.emit(ModelState.Error(R.string.wrong_code_format)) }
                        .collect { modelState.emit(ModelState.Success(it)) }
            }
        }
    }
}