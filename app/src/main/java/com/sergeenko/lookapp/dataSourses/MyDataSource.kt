package com.sergeenko.lookapp.dataSourses

import androidx.paging.DataSource
import kotlinx.coroutines.flow.MutableStateFlow

abstract class MyDataSource<T>: DataSource.Factory<Int, T>() {

    open fun invalidate(){

    }

    val lookDataSourceFlow = MutableStateFlow<MyPageKeyedDataSource<T>?>(null)
}
