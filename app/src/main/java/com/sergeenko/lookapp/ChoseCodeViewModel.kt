package com.sergeenko.lookapp

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.sergeenko.lookapp.models.Code
import com.sergeenko.lookapp.models.CodeDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ChoseCodeViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val countryCodeDao: CodeDao
) : BaseViewModel(repository, savedStateHandle) {

    fun choseCountryCode() {
        val countryCode = adapter.currentList?.find { countryCode -> countryCode.isSelected}
        if(modelState.value !is ModelState.Loading){
            viewModelScope.launch(IO) {
                modelState.emit(ModelState.Loading)
                restoreSelected(countryCode)
                countryCode?.let { countryCodeDao.update(it) }
                modelState.emit(ModelState.Success(null))
            }
        }
    }

    private fun restoreSelected(countryCode: Code?) {
        countryCodeDao.getSelected().forEach {
            if(it.id != countryCode?.id) {
                it.isSelected = false
                countryCodeDao.update(it)
            }
        }
    }

    fun collectData(): CountryCodeAdapter {
        viewModelScope.launch {
            countryCodeFlow
                .collect {
                    adapter.submitList(it)
                }
        }
        return adapter
    }

    private val adapter: CountryCodeAdapter by lazy {
        CountryCodeAdapter()
    }

    private val countryCodeFlow: Flow<PagedList<Code>> =
        countryCodeDao.getAll().toLiveData(50).asFlow()

}

