package com.sergeenko.lookapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FilterImageAdapter : RecyclerView.Adapter<FilterImageViewHolder>() {

    var fileList = listOf<File>()
    var selectedCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterImageViewHolder {
        return FilterImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.filter_image_view, null, false)
        )
    }

    override fun onBindViewHolder(holder: FilterImageViewHolder, position: Int) {
        holder.bind(
            file = fileList[position]
        )
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun setList(_fileList: List<File>) {
        fileList = _fileList
        notifyDataSetChanged()
    }

}