package com.sergeenko.lookapp

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sergeenko.lookapp.models.CodeDao

class PostViewModel @ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle,
        private val countryCodeDao: CodeDao
) : BaseViewModel(repository, savedStateHandle) {

    fun getImg(): Img {
        return repository.getImg(0,0,0, 0, false)
    }
}