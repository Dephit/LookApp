package com.sergeenko.lookapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GallaryImageAdapter(val onImageSelected: (File) -> Unit, val onImageAdd: (GallaryImage) -> Unit) : RecyclerView.Adapter<GallaryImageViewHolder>() {

    var fileList = listOf<GallaryImage>()
    var selectedCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GallaryImageViewHolder {
        return GallaryImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gallary_image_view, null, false))
    }

    override fun onBindViewHolder(holder: GallaryImageViewHolder, position: Int) {
        holder.bind(
                file = fileList[position],
                selectedCount = selectedCount,
                onImageSelected = onImageSelected,
                onImageAdd = {
                    selectedCount += if(it.isSelected) 1 else -1
                    onImageAdd(it)
                    if(selectedCount >= 10 || selectedCount == 9 && !it.isSelected){
                        notifyDataSetChanged()
                    }else {
                        notifyItemChanged(position)
                    }
                }
        )
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun setList(_fileList: List<GallaryImage>) {
        fileList = _fileList
    }

}

