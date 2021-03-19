package com.sergeenko.lookapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.LookErrorViewBinding

class LookErrorViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    inline fun bind(status: ModelState?, crossinline onError: () -> Unit) {
        with(LookErrorViewBinding.bind(view)){
            error.visibility = if (status is ModelState.Error<*>) View.VISIBLE else View.GONE
            error.setOnClickListener {
                onError()
            }
            imageView2.visibility = if (status is ModelState.Loading) View.VISIBLE else View.INVISIBLE
            progressBar2.visibility = if (status is ModelState.Loading) View.VISIBLE else View.INVISIBLE
        }
    }


}
