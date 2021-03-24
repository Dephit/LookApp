package com.sergeenko.lookapp

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.CommentViewBinding
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import kotlinx.coroutines.CoroutineScope
import java.util.logging.Level

class CommentsAdapter(
        val repository: Repository,
        private val level: Int = 0,
        val viewModelScope: CoroutineScope,
        private val onError: () -> Unit,
        val onCommentPress: (Comment) -> Unit,
        val onCommentSelected: (Comment) -> Unit,
        val onRespond: (Comment, Int) -> Unit) : MyBaseAdapter<Comment>(
    DIFF_CALLBACK
) {

    private val DATA_VIEW_TYPE = 1
    private val FOOTER_VIEW_TYPE = 2

    var selectedComment: Pair<CommentViewBinding?, Comment>? = null
    var selectedState: Int = 0

    fun clearSelection(){
        manageActivation(null, null, true)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentViewHolder -> {
                getItem(position)?.let { comment ->
                        holder.bind(
                                comment = comment,
                                repository = repository,
                                viewModelScope = viewModelScope,
                                onCommentPress = {view, com->
                                    selectedState = 1
                                    manageActivation(view, com, false)
                                    onCommentPress(com)
                                                 },
                                onRespond = {view, com ->
                                    selectedState = 3
                                    manageActivation(view, com, true)
                                    onRespond(com, position)
                                            },
                                onCommentSelected = {view, com->
                                    selectedState = 2
                                    manageActivation(view, com, true)
                                    onCommentSelected(com)
                                },
                                isSelected = Pair(selectedComment?.second, selectedState)
                        )
                }
            }
            is LookErrorViewHolder -> { holder.bind(getState(), onError) }
        }
    }

    private fun manageActivation(com: CommentViewBinding?, comment: Comment?, selection: Boolean) {
        selectedComment?.first?.bg?.apply {
            isSelected = false
            isActivated = false
        } ?: run {
            notifyDataSetChanged()
        }
        if(com == null){
            selectedComment = null
            selectedState = 0
        }else{
            selectedComment = Pair(com, comment!!)
            if(!selection)
                com.bg.isSelected = true
            else
                com.bg.isActivated = true
        }
    }

    override fun getItem(position: Int): Comment? {
        return super.getItem(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {
        private val DIFF_CALLBACK = object :
                DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldConcert: Comment, newConcert: Comment) = oldConcert.id == newConcert.id

            override fun areContentsTheSame(oldConcert: Comment, newConcert: Comment) = oldConcert == newConcert
        }
    }


    override fun getItemCount(): Int {
        return super.getItemCount() //+ if (hasFooter()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) DATA_VIEW_TYPE else FOOTER_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (getState() !is ModelState.Error<*> && getState() !is ModelState.Loading && itemCount > 0)
            CommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_view, parent, false))
        else
            LookErrorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.look_error_view, parent, false))
    }
}
