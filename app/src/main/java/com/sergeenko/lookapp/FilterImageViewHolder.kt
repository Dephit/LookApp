package com.sergeenko.lookapp

import android.net.Uri
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.FilterImageViewBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class FilterImageViewHolder(itemView: View, val height: Int, val onDelete: (FilterImage) -> Unit): RecyclerView.ViewHolder(itemView) {

    val binding: FilterImageViewBinding = FilterImageViewBinding.bind(itemView)

    init {
        binding.root.post {
            val lp = binding.root.layoutParams
            lp.width = height
            binding.root.layoutParams = lp
        }
    }

    fun bind(file: FilterImage){
        if(file.bitmap == null) {
            Picasso.get()
                .load(Uri.fromFile(file.file))
                .placeholder(R.drawable.look_img_background)
                .into(binding.img, object : Callback {
                    override fun onSuccess() {
                        file.bitmap = binding.img.drawable.toBitmap()
                        val lp = binding.img.layoutParams
                        lp.width = (lp.width * 0.5).toInt()
                        lp.height = (lp.height * 0.5).toInt()
                        binding.img.layoutParams = lp
                    }

                    override fun onError(e: Exception?) {

                    }

                })
        }else {
            if(file.bitmapFiltered == null){
                binding.img.setImageBitmap(file.bitmap)
            }else {
                binding.img.setImageBitmap(file.bitmapFiltered)
            }

        }
        binding.trash.setOnClickListener {
            onDelete(file)
        }

    }

}