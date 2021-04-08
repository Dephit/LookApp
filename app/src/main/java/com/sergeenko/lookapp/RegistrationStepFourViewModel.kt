package com.sergeenko.lookapp

import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sergeenko.lookapp.models.Profile
import com.sergeenko.lookapp.models.SocialResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class RegistrationStepFourViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(repository, savedStateHandle) {

    var chest: String? = "66"
    var waist: String? = "66"
    var hips: String? = "66"

    fun hips(newVal: String) {
        hips = newVal
        checkField()
    }

    fun waist(newVal: String) {
        waist = newVal
        checkField()
    }

    fun chest(newVal: String) {
        chest = newVal
        checkField()
    }

    fun checkField() {
        if (chest != null && waist != null && hips != null) {
            viewModelScope.launch {
                modelState.emit(ModelState.Success(true))
            }
        }
    }

    fun updateProfile(bundle: Bundle) {
        if(modelState.value !is ModelState.Loading) {
            viewModelScope.launch {
                val profile = Profile(
                        gender =  bundle.getString("gender", null),
                        height =  bundle.getString("height", null)?.toIntOrNull(),
                        weight =  bundle.getString("weight", null)?.toIntOrNull(),
                        hips =  bundle.getString("hips", null)?.toIntOrNull(),
                        chest =  bundle.getString("chest", null)?.toIntOrNull(),
                        waist =  bundle.getString("waist", null)?.toIntOrNull(),
                )

                doCollect(
                    repository.updateUser(login = bundle.getString("email", ""), profile = profile)
                )

            }
        }
    }
}
