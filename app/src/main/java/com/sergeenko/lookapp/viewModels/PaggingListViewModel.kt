package com.sergeenko.lookapp.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.sergeenko.lookapp.models.ModelState
import com.sergeenko.lookapp.dataSourses.MyDataSource
import com.sergeenko.lookapp.interfaces.Repository
import com.sergeenko.lookapp.adapters.MyBaseAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class PaggingListViewModel<U>(repository: Repository, savedStateHandle: SavedStateHandle) : BaseViewModel(repository, savedStateHandle) {

    lateinit var adapter: MyBaseAdapter<U>
    var isListInited = false

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
            if(!isListInited)
                lookList.collect {
                    isListInited = true
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
