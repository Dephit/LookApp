package com.sergeenko.lookapp

import kotlinx.coroutines.flow.MutableStateFlow

interface BaseViewModelInterface {
    fun getState() : MutableStateFlow<ModelState>

    suspend fun fetchBrands() {}
}
