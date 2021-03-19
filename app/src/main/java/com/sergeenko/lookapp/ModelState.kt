package com.sergeenko.lookapp

sealed class ModelState {
    class Success<T>(val obj: T? = null) : ModelState()
    class Error<T>(val obj: T? = null) : ModelState()
    object Loading : ModelState()
    object Normal : ModelState()
}

