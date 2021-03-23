package com.sergeenko.lookapp

import android.util.Log
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class MyBaseAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>): PagedListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    private var state: ModelState = ModelState.Loading

    fun getState(): ModelState = state

    fun setState(state: ModelState) {
        this.state = state
        notifyItemChanged(super.getItemCount())
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    protected fun hasFooter(): Boolean {
        return super.getItemCount() == 0 || state is ModelState.Loading || state is ModelState.Error<*>
    }

    override fun getItemCount(): Int {
        return super.getItemCount() 
    }
}
