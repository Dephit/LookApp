package com.sergeenko.lookapp

import com.sergeenko.lookapp.models.Look
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class FakeLookDataSource(
    private val repository: Repository,
    private val viewModelScope: CoroutineScope,
    private val errorState: MutableStateFlow<ModelState>
) : MyPageKeyedDataSource<FakeLook>(errorState) {

    override fun load(key: Int, requestedLoadSize: Int, onDone: (List<FakeLook>) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            repository.loadLooks(key, requestedLoadSize)
                .onStart { updateState(ModelState.Loading) }
                .catch {
                    updateState(ModelState.Error(null))
                    onError()
                }
                .collect {
                    updateState(ModelState.Success(null))
                    onDone(it)
                }
        }
    }
}

class LookDataSource(
    private val repository: Repository,
    private val viewModelScope: CoroutineScope,
    errorState: MutableStateFlow<ModelState>
) : MyPageKeyedDataSource<Look>(errorState) {

    override fun load(key: Int, requestedLoadSize: Int, onDone: (List<Look>) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            repository.getLooks(key, requestedLoadSize)
                .onStart { updateState(ModelState.Loading) }
                .catch {
                    updateState(ModelState.Error(null))
                    onError()
                }
                .collect {
                    updateState(ModelState.Success(null))
                    onDone(it)
                }
            }
        }
    }



