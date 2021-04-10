package com.sergeenko.lookapp.sourseFactories

import androidx.paging.DataSource
import com.sergeenko.lookapp.models.ModelState
import com.sergeenko.lookapp.dataSourses.MyDataSource
import com.sergeenko.lookapp.interfaces.Repository
import com.sergeenko.lookapp.dataSourses.FakeLookDataSource
import com.sergeenko.lookapp.dataSourses.LookDataSource
import com.sergeenko.lookapp.models.FakeLook
import com.sergeenko.lookapp.models.Look
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FakeLookDataSourceFactory(
        private val repository: Repository,
        private val viewModelScope: CoroutineScope,
        private val errorState: MutableStateFlow<ModelState>
) : MyDataSource<FakeLook>() {

    override fun create(): DataSource<Int, FakeLook> {
        val lookDataSource = FakeLookDataSource(repository, viewModelScope, errorState)
        viewModelScope.launch {
            lookDataSourceFlow.emit(lookDataSource)
        }
        return lookDataSource
    }
}

class LookDataSourceFactory(
        private val repository: Repository,
        private val viewModelScope: CoroutineScope,
        private val errorState: MutableStateFlow<ModelState>
) : MyDataSource<Look>() {

    override fun create(): DataSource<Int, Look> {
        val lookDataSource = LookDataSource(repository, viewModelScope, errorState)
        viewModelScope.launch {
            lookDataSourceFlow.emit(lookDataSource)
        }
        return lookDataSource
    }
}

