package com.sergeenko.lookapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.MarkUserElementBinding

class MarkUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = MarkUserElementBinding.bind(itemView)

    inline fun bind(s: String, crossinline onSave: (String) -> Unit) {
        with(binding){
            authorName.text = s
            autorImg.clipToOutline = true
            root.setOnClickListener {
                onSave("$s ")
            }
        }
    }

}
