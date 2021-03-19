package com.sergeenko.lookapp

import android.os.Bundle

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
