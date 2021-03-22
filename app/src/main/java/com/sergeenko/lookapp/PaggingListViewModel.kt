package com.sergeenko.lookapp

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class PaggingListViewModel<U>(repository: Repository, savedStateHandle: SavedStateHandle) : BaseViewModel(repository, savedStateHandle) {

    lateinit var adapter: MyBaseAdapter<U>

    abstract fun createAdapter(): MyBaseAdapter<U>

    val errorState = MutableStateFlow<ModelState>(ModelState.Success(null))
    lateinit var lookList: Flow<PagedList<U>>
    val pageSize = 10
    val newsDataSourceFactory: MyDataSource<U> by lazy{
        getDataSourceFactory()
    }

    abstract fun getDataSourceFactory(): MyDataSource<U>

    fun retry() {
        newsDataSourceFactory.lookDataSourceFlow.value?.retryCall()
    }

    fun collectData(): MyBaseAdapter<U> {
        viewModelScope.launch {
            lookList.collect {
                adapter.submitList(it)
            }
        }
        return adapter
    }

    fun collectState() {
        viewModelScope.launch {
            errorState.collect {
                adapter.setState(it)
            }
        }
    }
}
