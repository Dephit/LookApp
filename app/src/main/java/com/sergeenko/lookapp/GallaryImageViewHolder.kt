package com.sergeenko.lookapp

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.GallaryImageViewBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File

class GallaryImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val binding: GallaryImageViewBinding = GallaryImageViewBinding.bind(itemView)

    fun bind(file: GallaryImage, onImageSelected: (Uri) -> Unit, onImageAdd: (GallaryImage) -> Unit, selectedCount: Int){
        if(file.drawable != null){
            binding.img.setImageDrawable(file.drawable)
        }else{
            Picasso.get()
                    .load(file.file)
                    .placeholder(R.drawable.look_img_background)
                    .into(binding.img, object : Callback{
                        override fun onSuccess() {
                            file.drawable = binding.img.drawable
                        }

                        override fun onError(e: Exception?) {

                        }

                    })
        }

        if(file.isSelected || selectedCount < 10){
            binding.selectedToggle.visibility = View.VISIBLE
        }else {
            binding.selectedToggle.visibility = View.GONE
        }

        binding.selectedToggle.isSelected = file.isSelected

        binding.selectedToggle.setOnClickListener {
            file.isSelected = !file.isSelected
            onImageAdd(file)
        }

        binding.img.setOnClickListener {
            onImageSelected(file.file)
        }
    }

}

