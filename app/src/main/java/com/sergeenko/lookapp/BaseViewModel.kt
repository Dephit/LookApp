package com.sergeenko.lookapp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class BaseViewModel constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
): ViewModel(), BaseViewModelInterface {

    @ExperimentalCoroutinesApi
    protected val modelState: MutableStateFlow<ModelState> = MutableStateFlow(ModelState.Normal)

    @ExperimentalCoroutinesApi
    override fun getState(): MutableStateFlow<ModelState> = modelState
    fun restoreState() {
        modelState.value = ModelState.Normal
    }

    override suspend fun fetchBrands() {
        //viewModelScope.launch(IO) {
        repository.getBrands()
        repository.getBrands()
        //}
    }

    @ExperimentalCoroutinesApi
    suspend fun<T> doCollect(flow: Flow<T>) {
        flow.onStart { modelState.emit(ModelState.Loading)}
                .catch { modelState.emit(ModelState.Error(it.message)) }
                .collect { modelState.emit(ModelState.Success(it)) }
    }
}
