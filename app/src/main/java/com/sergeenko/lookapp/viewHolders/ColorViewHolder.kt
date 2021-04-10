package com.sergeenko.lookapp.viewHolders

import android.graphics.Color
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.ColorViewBinding

class ColorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val binding = ColorViewBinding.bind(itemView)

    fun bind(i: String, isSelected: Boolean, function: (String) -> Unit) {
        binding.colorImg.setImageDrawable(Color.parseColor(i).toDrawable())
        binding.colorImg.clipToOutline = true
        binding.isSelected.visibility = if(isSelected) View.VISIBLE else View.GONE

        binding.root.setOnClickListener {
            function(i)
        }
    }

}
