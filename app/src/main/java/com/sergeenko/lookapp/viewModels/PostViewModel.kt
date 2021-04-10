package com.sergeenko.lookapp.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import com.sergeenko.lookapp.interfaces.Repository
import com.sergeenko.lookapp.models.CodeDao
import com.sergeenko.lookapp.models.Img

class PostViewModel @ViewModelInject constructor(
        private val repository: Repository,
        private val countryCodeDao: CodeDao
) : BaseViewModel(repository) {

    fun getImg(): Img {
        return repository.getImg(0,0,0, 0, false)
    }
}