package com.sergeenko.lookapp.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import com.sergeenko.lookapp.interfaces.Repository

class RegistrationStepOneViewModel @ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(repository, savedStateHandle) {

    var gender: String? = null
    var genderNum: Int? = null

    fun selectSex(indexOfChild: Int) {
        when(indexOfChild){
            0 -> {
                gender = "male"
                genderNum = 0
            }
            1 -> {
                gender = "female"
                genderNum = 1
            }
            2 -> {
                gender = "other"
                genderNum = 2
            }
        }
    }

}