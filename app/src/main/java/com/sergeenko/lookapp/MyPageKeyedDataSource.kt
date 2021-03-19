package com.sergeenko.lookapp

import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.flow.MutableStateFlow

abstract class MyPageKeyedDataSource<T>(
        private val errorState: MutableStateFlow<ModelState>
): PageKeyedDataSource<Int, T>() {
    private lateinit var retry: () -> Unit

    abstract fun load(key: Int, requestedLoadSize: Int, onDone: (List<T>) -> Unit, onError: () -> Unit)

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
        load(1, params.requestedLoadSize,
                onDone = {  callback.onResult(it, null, 2) },
                onError = { setRetry { loadInitial(params, callback) } }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        load(params.key, params.requestedLoadSize,
                onDone = { list-> callback.onResult(list, params.key + 1) },
                onError = { setRetry { loadAfter(params, callback) } }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        load(params.key, params.requestedLoadSize,
                onDone = { list-> callback.onResult(list, params.key - 1) },
                onError = { setRetry { loadAfter(params, callback) } }
        )
    }

    suspend fun updateState(state: ModelState) {
        this.errorState.emit(state)
    }

    private fun setRetry(function: () -> Unit) {
        retry = function
    }

    fun retryCall() {
        if (this::retry.isInitialized) {
            retry()
        }
    }
}
