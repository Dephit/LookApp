package com.sergeenko.lookapp.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import com.sergeenko.lookapp.interfaces.Repository

class NewPostViewModel  @ViewModelInject constructor(
        private val repository: Repository,
) : BaseViewModel(repository) {
}