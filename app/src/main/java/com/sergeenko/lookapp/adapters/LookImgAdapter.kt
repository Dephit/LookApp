package com.sergeenko.lookapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.viewHolders.ImgViewHolder
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.models.Image


class LookImgAdapter(var list: List<Image>) : RecyclerView.Adapter<ImgViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder {
        return ImgViewHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.look_img,
                        parent,
                        false
                )
        )
    }

    var onRootPressed: (View)-> Unit = {}

    fun updateList(_list: List<Image>, _onRootPressed: (View)-> Unit){
        onRootPressed = _onRootPressed
        list = _list
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ImgViewHolder, position: Int) {
        holder.bind(list[position], onRootPressed)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
