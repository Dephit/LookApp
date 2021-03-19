package com.sergeenko.lookapp

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sergeenko.lookapp.models.Look
import kotlinx.coroutines.launch

class LookScrollingViewModel @ViewModelInject constructor(
        val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : PaggingListViewModel<Look>(repository, savedStateHandle) {

    init {
        adapter = createAdapter()
        val config = PagedList.Config.Builder()
                .setPageSize(pageSize)
                .setInitialLoadSizeHint(pageSize * 2)
                .setEnablePlaceholders(false)
                .build()
        lookList = LivePagedListBuilder(newsDataSourceFactory, config).build().asFlow()
    }

    override fun createAdapter(): MyBaseAdapter<Look> {
        return LookAdapter(viewModelScope = viewModelScope, repository = repository) { retry() }
    }

    override fun getDataSourceFactory(): MyDataSource<Look> {
        return LookDataSourceFactory(repository, viewModelScope, errorState)
    }

}

