package com.sergeenko.lookapp.viewModels

import android.text.Editable
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sergeenko.lookapp.models.ModelState
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.interfaces.Repository
import com.sergeenko.lookapp.models.Code
import com.sergeenko.lookapp.models.CodeDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.lang.Exception

class PhoneAuthViewModel @ViewModelInject constructor(
        private val repository: Repository,
        private val countryCodeDao: CodeDao
) : BaseViewModel(repository) {

    var phone: String = ""
    var selectedCode: Code? = null

    @ExperimentalCoroutinesApi
    fun authByPhone(phone: Editable?) {
        val ph =  phone.toString().replace(("[\\D]").toRegex(), "")

        if(modelState.value !is ModelState.Loading)
            viewModelScope.launch {
                try {
                    if(!isPhoneWithError(ph)){
                        repository.authByPhone("${selectedCode!!.dialCode}$ph")
                                .onStart { modelState.emit(ModelState.Loading) }
                                .catch { modelState.emit(ModelState.Error(it.message)) }
                                .collect { modelState.emit(ModelState.Success("${selectedCode!!.dialCode}$ph")) }
                    }
                }catch (e: Exception){
                    modelState.emit(ModelState.Error("Ошибка"))
                }

        }
    }

    @ExperimentalCoroutinesApi
    suspend fun isPhoneWithError(ph: String): Boolean {
        return when {
            ph.isEmpty() -> {
                modelState.emit(ModelState.Error(R.string.empty_field_error))
                true
            }
            ph.length < selectedCode!!.mask!!.count { it == '#' } -> {
                modelState.emit(ModelState.Error(R.string.wrong_phone_format))
                true
            }
            else -> false
        }
    }

    fun getCurrentCode(): Code? {
        selectedCode = countryCodeDao.getSelected().firstOrNull()
        return selectedCode
    }

    fun getHint(): String? {
        var str = selectedCode?.mask?.replace(selectedCode!!.dialCode!!, "")
        str = str?.replace("+", "")
        str = str?.replace(("[\\d]").toRegex(), "")
        str = str?.replace("()","")
        if(str?.first() == '-'){
            str = str.replaceFirst("-","")
        }

        return str?.trim()
    }

    fun getFullHint(): String? {
        return " ${selectedCode?.mask?.replace("#", "_")?.replace("+", "")?.replace(("[\\d]").toRegex(), "")}"
    }

}