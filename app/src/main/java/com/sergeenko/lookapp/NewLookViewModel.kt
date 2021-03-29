package com.sergeenko.lookapp

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class NewLookViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : BaseViewModel(repository, savedStateHandle) {

    val fileList = mutableListOf<GallaryImage>()
    val selectedList = mutableListOf<File>()

    fun savePhoto() {
        if(selectedList.isNotEmpty()) {
            viewModelScope.launch {
                modelState.emit(ModelState.Success(true))
            }
        }
    }

    val adapter: GallaryImageAdapter by lazy{
        GallaryImageAdapter(
                onImageSelected = ::selectImage,
                onImageAdd = ::onImageAdd
        )
    }

    fun onImageAdd(file: GallaryImage){
        if(file.isSelected){
            selectedList.add(file.file)
        }else {
            selectedList.remove(file.file)
        }
    }

    private fun selectImage(file: File){
        viewModelScope.launch {
            modelState.emit(ModelState.Success(Uri.fromFile(file)))
        }
    }

    fun loadSavedImages() {
        viewModelScope.launch {
            val dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            if (dcimPath.exists()) {
                dcimPath.list { dir, name ->
                    File("${dir.absolutePath}/${name}").listFiles()?.forEach {
                        if(it.name.contains(".jpg") || it.name.contains(".jpeg") || it.name.contains(".png"))
                            fileList.add(GallaryImage(it, false))
                    }
                    return@list true
                }
            } else {
                Log.i("IMAGES", "Can't find location")
            }
            modelState.emit(ModelState.Success(fileList))
        }
    }
}