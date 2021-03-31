package com.sergeenko.lookapp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import java.io.File

class FilterImageAdapter : RecyclerView.Adapter<FilterImageViewHolder>() {

    var fileList = listOf<FilterImage>()
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

    fun setList(_fileList: List<FilterImage>) {
        fileList = _fileList
        notifyDataSetChanged()
    }

    fun getCurrentFile(position: Int) = fileList[position]

    fun applyFilter(position: Int, filter: Filter?): Boolean {
        return try {
            val file = fileList[position]
            if(file.filter == filter){
                file.bitmapFiltered = null
                file.filter = null
            }else{
                file.bitmapFiltered = filter?.processFilter(file.bitmap?.copy(Bitmap.Config.ARGB_8888, true))
                file.filter = filter

            }
            notifyItemChanged(position)
            file.filter != null
        }catch (e: Exception){
            false
        }
    }

}

data class FilterImage(
    val file: File,
    var filter: Filter? = null,
    var bitmap: Bitmap? = null,
    var bitmapFiltered: Bitmap? = null
)
