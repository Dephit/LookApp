package com.sergeenko.lookapp.viewModels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.sergeenko.lookapp.models.ModelState
import com.sergeenko.lookapp.interfaces.Repository
import com.sergeenko.lookapp.adapters.FilterImageAdapter
import com.sergeenko.lookapp.adapters.OrientationAdapter
import com.sergeenko.lookapp.adapters.RotationMode
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import kotlinx.coroutines.launch

sealed class SettngsScreenState{
    object Orientation: SettngsScreenState()
    object Brightness: SettngsScreenState()
    object Background: SettngsScreenState()
    object Contrast: SettngsScreenState()
    object Settings: SettngsScreenState()
    object Filters: SettngsScreenState()
}

class FiltersViewModel@ViewModelInject constructor(
        private val repository: Repository,
        val orientationAdapter: OrientationAdapter,
        val adapter: FilterImageAdapter,
) : BaseViewModel(repository) {

    var screenState: SettngsScreenState = SettngsScreenState.Filters
    var thumbs: MutableList<ThumbnailItem> = mutableListOf()

    init {
        adapter.scope = viewModelScope
    }

    fun applyFilter(position: Int, filter: Filter?): Boolean {
        return adapter.applyFilter(position, filter)
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

    fun getCurrentRotation(currentPosition: Int): CharSequence? {
        return try {
            when (adapter.rotationMode) {
                is RotationMode.RotationX -> { adapter.getCurrentFile(currentPosition).rotationX.toString() }
                is RotationMode.RotationY -> { adapter.getCurrentFile(currentPosition).rotationY.toString() }
                else -> { adapter.getCurrentFile(currentPosition).rotationZ.toString() }
            }
        }catch (e: Exception){
            "0.0"
        }
    }
}