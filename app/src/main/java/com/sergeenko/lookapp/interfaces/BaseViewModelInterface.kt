package com.sergeenko.lookapp.interfaces

import com.sergeenko.lookapp.models.ModelState
import kotlinx.coroutines.flow.MutableStateFlow

interface BaseViewModelInterface {
    fun getState() : MutableStateFlow<ModelState>

    suspend fun fetchBrands() {}
}
