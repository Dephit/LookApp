package com.sergeenko.lookapp.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sergeenko.lookapp.interfaces.BaseViewModelInterface
import com.sergeenko.lookapp.models.ModelState
import com.sergeenko.lookapp.interfaces.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

open class BaseViewModel constructor(
        private val repository: Repository
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
