package com.sergeenko.lookapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.models.ModelState
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.interfaces.Repository
import com.sergeenko.lookapp.models.Look
import com.sergeenko.lookapp.viewHolders.LookErrorViewHolder
import com.sergeenko.lookapp.viewHolders.LookViewHolder
import kotlinx.coroutines.CoroutineScope
import java.lang.Exception

class LookAdapter(
        val viewModelScope: CoroutineScope,
        val repository: Repository,
        private val onError: () -> Unit
) : MyBaseAdapter<Look>(DIFF_CALLBACK) {

    var disableScroll: (Boolean) -> Unit = {

    }

    private var h: Int = 0
    private val DATA_VIEW_TYPE = 1
    private val FOOTER_VIEW_TYPE = 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LookViewHolder -> { getItem(position)?.let { holder.bind(it, viewModelScope = viewModelScope, repository = repository, h = h, disableScroll = disableScroll) } }
            is LookErrorViewHolder -> { holder.bind(getState(), onError) }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()// + if (hasFooter()) 1 else 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun setHeight(height: Int) {
        h = height
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Look>() {
            override fun areItemsTheSame(oldConcert: Look, newConcert: Look) = oldConcert.id == newConcert.id

            override fun areContentsTheSame(oldConcert: Look, newConcert: Look) = oldConcert == newConcert
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount) DATA_VIEW_TYPE else FOOTER_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (getState() !is ModelState.Error<*> && getState() !is ModelState.Loading && itemCount > 0) LookViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.look_view, parent, false)
        ) else LookErrorViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.look_error_view, parent, false)
        )
    }

    override fun isPostOpen(lastPostion: Int): Boolean {
        return try {
            val look = currentList?.get(lastPostion)
            look?.type != "Лук" && look?.isPostOpen == true
        }catch (e: Exception){
            false
        }
    }

    override fun closeLastView(lastPostion: Int) {
        try {
            val look = currentList?.get(lastPostion)
            if(look?.type != "Лук" && look?.isPostOpen == true)
                notifyItemChanged(lastPostion)
        }catch (e: Exception){

        }
    }
}


