package com.sergeenko.lookapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.CommentViewBinding
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class CommentViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

    val binding = CommentViewBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(
            comment: Comment,
            repository: Repository,
            level: Int = 0,
            viewModelScope: CoroutineScope,
            onCommentPress: (Comment) -> Unit,
            onCommentSelected: (Comment) -> Unit,
            onRespond: (Comment) -> Unit,
            isSelected: Int){
        with(binding){
            Picasso.get()
                    .load(comment.user.avatar)
                    .noPlaceholder()
                    .into(avatar)
            avatar.clipToOutline = true
            dateText.text = comment.created

            if(comment.isPost){
                divider.visibility = View.VISIBLE
                respondText.visibility = View.GONE
            }else {
                divider.visibility = View.GONE
                respondText.visibility = View.VISIBLE
            }

            bg.isActivated = isSelected == comment.id

            if(level == 0){
                if(comment.count_comments > 0)
                    addComments(level = level + 1, comment = comment, repository = repository, viewModelScope = viewModelScope, onRespond= onRespond)
            }else if(level > 0){
                binding.comment.setPadding(itemView.context.resources.getDimension(R.dimen._40sdp).roundToInt(), 0,0,0)
            }else{
                //binding.comment.setPadding(0,0,0,0)
            }

            val text = "${comment.user.username} ${comment.text}"
            try {
                setSpannableText(commentText, text, comment.user.username, scrollToParent = {
                    //scrollToParent(comment.id)
                })
            }catch (e: Exception){ }

            avatar.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.action_commentsFragment_to_profileFragment)
            }

            respondText.setOnClickListener {
                onCommentSelected(comment)
                onRespond(comment)
            }

            root.setOnClickListener {
                onCommentPress(comment)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(comment: Look, onCommentPress: (Comment) -> Unit){
        with(binding){
            Picasso.get()
                    .load(comment.user.avatar)
                    .noPlaceholder()
                    .into(avatar)
            avatar.clipToOutline = true

            val text = "${comment.user.username} ${comment.title}"
            //setSpannableText(comment, commentText, text, comment.user.username)
        }
    }

    private fun addComments(level: Int, comment: Comment, repository: Repository, viewModelScope: CoroutineScope, onRespond: (Comment) -> Unit) {
        val llm = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        binding.commentsList.layoutManager = llm
        viewModelScope.launch {
            repository.getComments(commentId = comment.id, page = 1)
                    .catch {  }
                    .collect {
                        if(it.data.isNotEmpty()){
                            binding.commentsList.adapter = CommentsListAdapter(
                                    selectedComment = -1,
                                    repository = repository,
                                    viewModelScope = viewModelScope,
                                    list = it.data,
                                    level = level,
                                    onCommentPress = {},
                                    onRespond = onRespond,
                                    onCommentSelected = {},
                                    onError = {}
                            )
                        }
                    }
        }

      /*  viewModelScope.launch {
            val adapter = CommentsAdapter(
                    repository = repository,
                    level = level,
                    viewModelScope = viewModelScope,
                    onCommentPress = {},
                    onCommentSelected = {},
                    onError = {},
                    onRespond = {adapter, comment -> }
            )

            val errorState = MutableStateFlow<ModelState>(ModelState.Success(null))

            val config = PagedList.Config.Builder()
                    .setPageSize(10)
                    .setInitialLoadSizeHint(10 * 2)
                    .setEnablePlaceholders(false)
                    .build()

            val lookList = LivePagedListBuilder(CommentDataSourceFactory(comment = comment, repository = repository, viewModelScope = viewModelScope, errorState = errorState), config).build().asFlow()

            val llm = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            binding.commentsList.layoutManager = llm
            binding.commentsList.adapter =  adapter

            lookList.collect {
                adapter.submitList(it)
            }
            errorState.collect {
                adapter.setState(it)
            }
        }*/
    }


    private fun setSpannableText(heightText: TextView, textID: String, toSpan: String, scrollToParent: ()-> Unit = {}) {
        val spannable = SpannableString(textID)
        val ss = StyleSpan(Typeface.BOLD)

        val indexStart = textID.indexOf(toSpan)
        val indexEnd = textID.indexOf(toSpan) + toSpan.length

        val flag = Spannable.SPAN_INCLUSIVE_INCLUSIVE

        try {
            val boldSpan = ForegroundColorSpan(getColor(heightText.context, R.color.pink))
            val start: Int = textID.indexOf("#")
            val end: Int = textID.indexOf(" ", startIndex = start)
            spannable.setSpan(boldSpan, start, end, flag)
        }catch (e: Exception){

        }

        try {
            val boldSpan = ForegroundColorSpan(getColor(heightText.context, R.color.pink))
            val start: Int = textID.indexOf("@")
            val end: Int = textID.indexOf(" ", startIndex = start)
            spannable.setSpan(boldSpan, start, end, flag)

            val fc = object : ClickableSpan(){
                override fun onClick(widget: View) {
                    scrollToParent()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.linkColor = Color.GREEN
                }
            }
            spannable.setSpan(fc, start, end, flag)
        }catch (e: Exception){

        }

        spannable.setSpan(ss, indexStart, indexEnd, flag)

        heightText.movementMethod = LinkMovementMethod.getInstance()
        heightText.text = spannable
    }
}
