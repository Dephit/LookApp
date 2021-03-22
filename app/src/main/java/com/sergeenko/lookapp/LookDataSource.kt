package com.sergeenko.lookapp

import com.sergeenko.lookapp.models.Look
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class LookDataSource(
    private val repository: Repository,
    private val viewModelScope: CoroutineScope,
    errorState: MutableStateFlow<ModelState>
) : MyPageKeyedDataSource<Look>(errorState) {

    var lastID: Int? = null

    override fun load(key: Int, requestedLoadSize: Int, onDone: (List<Look>) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            repository.getLooks(key, requestedLoadSize)
                .onStart { updateState(ModelState.Loading) }
                .catch {
                    updateState(ModelState.Error(null))
                    onError()
                }
                .collect {
                    if(it.isEmpty()){
                        updateState(ModelState.Error(null))
                    }else{
                        if(it.last().id != lastID && it.isNotEmpty()) {
                            lastID = it.last().id
                            updateState(ModelState.Success(null))
                            onDone(it)
                        }else{
                            updateState(ModelState.Error("NoItems"))
                            onError()
                        }
                    }
                }
            }
        }
    }



