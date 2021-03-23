package com.sergeenko.lookapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import kotlinx.coroutines.CoroutineScope
import java.util.logging.Level

class CommentsAdapter(
        val repository: Repository,
        val level: Int = 0,
        val viewModelScope: CoroutineScope,
        private val onError: () -> Unit,
        val onCommentPress: (Comment) -> Unit,
        val onCommentSelected: (Comment) -> Unit,
        val onRespond: (Comment, Int) -> Unit) : MyBaseAdapter<Comment>(
    DIFF_CALLBACK
) {

    private val DATA_VIEW_TYPE = 1
    private val FOOTER_VIEW_TYPE = 2

    var selectedComment: Int? = null

    fun clearSelection(){
        selectedComment = 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentViewHolder -> {
                getItem(position)?.let { comment ->
                        holder.bind(
                                comment = comment,
                                repository = repository,
                                viewModelScope = viewModelScope,
                                level = level,
                                onCommentPress = {onCommentPress},
                                onRespond = { com ->
                                    selectedComment = com.id
                                    onRespond(com, position)
                                    notifyDataSetChanged()
                                            },
                                onCommentSelected = {com->
                                    selectedComment = com.id
                                    onCommentSelected(com)
                                    notifyDataSetChanged()
                                },
                                isSelected = selectedComment?: -1
                        )
                }
            }
            is LookErrorViewHolder -> { holder.bind(getState(), onError) }
        }
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


class CommentsListAdapter(var selectedComment: Int,
                          var list: List<Comment> ,
                          val repository: Repository,
                          val level: Int = 0,
                          val viewModelScope: CoroutineScope,
                          private val onError: () -> Unit,
                          val onCommentPress: (Comment) -> Unit,
                          val onCommentSelected: (Comment) -> Unit,
                          val onRespond: (Comment) -> Unit) : RecyclerView.Adapter<CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.comment_view, parent, false)
        )
    }

    private fun updateList(_list: List<Comment>){
        list = _list
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(
                list[position],
                repository = repository,
                viewModelScope = viewModelScope,
                level = level ,
                onCommentPress = {onCommentPress},
                onRespond = { com ->
                    selectedComment = com.id
                    onRespond(com)
                    notifyDataSetChanged()
                },
                onCommentSelected = {com->
                    selectedComment = com.id
                    onCommentSelected(com)
                    notifyDataSetChanged()
                },
                isSelected = selectedComment?: -1
        )
    }

    fun addComment(it: Comment) {
        updateList(list.toMutableList().apply { add(it) })
    }

}
