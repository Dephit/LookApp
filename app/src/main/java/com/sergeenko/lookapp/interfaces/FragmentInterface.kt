package com.sergeenko.lookapp.interfaces

import android.os.Bundle
import com.sergeenko.lookapp.models.ModelState

interface FragmentInterface<T> {
    fun observe()
    fun observeTo(modelState: ModelState?)
    fun restoreState(savedInstanceState: Bundle?)
    fun setListeners()
    fun manageLoading(b: Boolean)
    fun<T> manageSuccess(obj: T? = null)
    fun manageError(bool: Boolean)
    fun<T> manageError(error: T?)
}
