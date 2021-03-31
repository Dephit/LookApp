package com.sergeenko.lookapp

import android.util.Log
import com.sergeenko.lookapp.models.*
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

    var lastID: PostResponse? = null
    var links: Links? = null

    override fun load(key: Int, requestedLoadSize: Int, onDone: (List<Look>) -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            Log.i("ASDASDASD", "${key - 1} ${lastID?.meta?.current_page}")
            if(lastID != null && lastID!!.data.size  < requestedLoadSize){
                updateState(ModelState.Success(null))
                onDone(lastID!!.data)
            }else{
                repository.getLooks(key)
                        .onStart {
                        //    updateState(ModelState.Loading)
                        }
                        .catch {
                            //updateState(ModelState.Error(null))
                            //onError()
                        }
                        .collect {
                            if (it.links.last != links?.prev) {
                                //lastID = it
                                updateState(ModelState.Success(null))
                                links = it.links
                                onDone(it.data)
                            } else {
                                updateState(ModelState.Error("NoItems"))
                                onDone(it.data)
                            }
                        }
            }
        }
    }
}



