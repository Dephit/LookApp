package com.sergeenko.lookapp

import android.graphics.Bitmap
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.FinalViewImageViewBinding

class FinalLookViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val binding = FinalViewImageViewBinding.bind(itemView)

    fun bind(bitmap: Bitmap?){
        binding.imageView13.setImageBitmap(bitmap)
        binding.imageView13.clipToOutline = true
    }
}
