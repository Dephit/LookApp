package com.sergeenko.lookapp

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zomato.photofilters.imageprocessors.Filter
import kotlinx.coroutines.launch

sealed class SettngsScreenState(){
    object Orientation: SettngsScreenState()
    object Brightness: SettngsScreenState()
    object Background: SettngsScreenState()
    object Contrast: SettngsScreenState()
    object Settings: SettngsScreenState()
    object Filters: SettngsScreenState()
}

class FiltersViewModel@ViewModelInject constructor(
        private val repository: Repository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(repository, savedStateHandle) {

    var screenState: SettngsScreenState = SettngsScreenState.Filters

    fun applyFilter(position: Int, filter: Filter?): Boolean {
        //viewModelScope.launch {
        return adapter.applyFilter(position, filter)
        //}
    }

    fun delete(currentPosition: Int) {
        adapter.delete(currentPosition)
        viewModelScope.launch {
            modelState.emit(ModelState.Success("Delete"))
        }
    }

    fun setState(orientation: SettngsScreenState) {
        screenState = orientation
        viewModelScope.launch {
            modelState.emit(ModelState.Success(orientation))
        }
    }

    var width = 0

    val adapter: FilterImageAdapter by lazy{
        FilterImageAdapter(width)
    }
}