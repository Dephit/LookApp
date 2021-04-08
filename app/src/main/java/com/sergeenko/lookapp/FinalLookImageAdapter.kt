package com.sergeenko.lookapp

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FinalLookImageAdapter : RecyclerView.Adapter<FinalLookViewHolder>() {

    var list: List<Bitmap?> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinalLookViewHolder {
        return FinalLookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.final_view_image_view, null, false))
    }

    override fun onBindViewHolder(holder: FinalLookViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun updateList(_list: List<Bitmap?>){
        list = _list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
