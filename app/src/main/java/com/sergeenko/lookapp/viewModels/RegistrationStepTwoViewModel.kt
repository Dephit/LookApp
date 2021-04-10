package com.sergeenko.lookapp.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import com.sergeenko.lookapp.interfaces.Repository

class RegistrationStepTwoViewModel @ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(repository, savedStateHandle) {

    var height: String? = "180"

    fun height(newVal: String) {
        height = newVal
    }

}