package com.sergeenko.lookapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.LookErrorViewBinding

class LookErrorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    inline fun bind(status: ModelState?, crossinline onError: () -> Unit) {
        with(LookErrorViewBinding.bind(itemView)){
            error.visibility = if (status is ModelState.Error<*>) View.VISIBLE else View.GONE
            error.setOnClickListener {
                onError()
            }
            if (status is ModelState.Error<*>) {
                if (status.obj is String) {
                    error.text = itemView.context.getString(R.string.no_items)
                }
            }
            imageView2.visibility = if (status is ModelState.Loading) View.VISIBLE else View.INVISIBLE
            progressBar2.visibility = if (status is ModelState.Loading) View.VISIBLE else View.INVISIBLE
        }
    }


}
