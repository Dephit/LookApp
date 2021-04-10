package com.sergeenko.lookapp.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sergeenko.lookapp.adapters.LookAdapter
import com.sergeenko.lookapp.sourseFactories.LookDataSourceFactory
import com.sergeenko.lookapp.dataSourses.MyDataSource
import com.sergeenko.lookapp.interfaces.Repository
import com.sergeenko.lookapp.adapters.MyBaseAdapter
import com.sergeenko.lookapp.models.Look

class LookScrollingViewModel @ViewModelInject constructor(
        val repository: Repository,
) : PaggingListViewModel<Look>(repository) {

    var lastPostion: Int? = null

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

    fun setScreenHeight(height: Int) {
        adapter.setHeight(height)
    }

}

