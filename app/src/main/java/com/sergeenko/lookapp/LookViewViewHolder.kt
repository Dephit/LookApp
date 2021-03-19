package com.sergeenko.lookapp

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class LookViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    var handler: Handler = Handler(Looper.myLooper()!!)
    val more = getString(R.string.more)

    fun getString(total: Int): String {
        return itemView.context.getString(total)
    }
}