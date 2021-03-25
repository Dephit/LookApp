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
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.size
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.CommentViewBinding
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class CommentViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

    val binding = CommentViewBinding.bind(itemView)

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    fun bind(
            comment: Comment,
            repository: Repository,
            viewModelScope: CoroutineScope,
            onCommentPress: (CommentViewBinding, Comment) -> Unit,
            onCommentSelected: (CommentViewBinding,Comment) -> Unit,
            onRespond: (CommentViewBinding, Comment) -> Unit,
            parentView: CommentViewBinding? = null,
            parent: Comment? = null,
            isSelected: Pair<Comment?, Int>?){
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

            if(isSelected != null && isSelected.first?.id == comment.id){
                when (isSelected.second) {
                    1 -> {
                        onCommentPress(binding, comment)
                    }
                    2 -> {
                        onCommentSelected(binding, comment)
                    }
                    3 -> {
                        onCommentSelected(binding, comment)
                    }
                }
            }

            bg.isSelected = isSelected?.first?.id == comment.id
            bg.isActivated = isSelected?.first?.id == comment.id

            if(comment.has_comments) {
               setAddComments(
                   comment = comment,
                   repository = repository,
                   viewModelScope = viewModelScope,
                   onCommentSelected = onCommentSelected,
                   onRespond = onRespond,
                   onCommentPress = onCommentPress,
                   isSelected = isSelected)
            }else {
                commentList.removeAllViews()
            }

            if(comment.parent_id != null){
                binding.comment.setPadding(itemView.context.resources.getDimension(R.dimen._40sdp).roundToInt(), 0, 0, 0)
                addMore.visibility = View.GONE
            }else{
                binding.comment.setPadding(0, 0, 0, 0)
                if(comment.count_comments > 2)
                    addMore.visibility = View.VISIBLE
                else
                    addMore.visibility = View.GONE
            }

            val text = "${comment.user.username} ${comment.text}"

            try {
                setSpannableText(commentText,
                        text,
                        comment.user.username,
                        scrollToParent = {
                            if (parentView != null) {
                                if (parent != null) {
                                    onCommentSelected(parentView, parent)
                                }
                            } },
                        onCommentPress = {
                            onCommentPress(it, comment)
                        }
                )
            }catch (e: Exception){ }

            avatar.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.action_commentsFragment_to_profileFragment)
            }

            respondText.setOnClickListener {
                //onCommentSelected(binding, comment)
                onRespond(binding, comment)
            }

            root.setOnClickListener {
                onCommentPress(binding, comment)
            }
        }
    }

    private fun setAddComments(
        isSelected: Pair<Comment?, Int>?,
        comment: Comment,
        repository: Repository,
        viewModelScope: CoroutineScope,
        onRespond: (CommentViewBinding, Comment) -> Unit,
        onCommentPress: (CommentViewBinding, Comment) -> Unit,
        onCommentSelected: (CommentViewBinding,Comment) -> Unit,
    ) {
        val inflayer = LayoutInflater.from(itemView.context)
        binding.commentList.removeAllViews()
        comment.comments.forEach {
            val commentViewBinding = CommentViewHolder(inflayer.inflate(R.layout.comment_view, null,false))
            it.parent = comment
            commentViewBinding.bind(
                comment = it,
                repository = repository,
                viewModelScope = viewModelScope,
                onCommentPress = onCommentPress,
                parentView = binding,
                    parent = comment,
                onCommentSelected = onCommentSelected,
                onRespond = onRespond,
                isSelected = isSelected
            )
            binding.commentList.addView(commentViewBinding.binding.root)
        }
        var totalLeft = 2

        binding.addMore.setOnClickListener {
            if(totalLeft == 2){
                binding.commentList.removeAllViews()
            }
            addComments(
                comment = comment,
                repository = repository,
                viewModelScope = viewModelScope,
                onRespond = onRespond,
                onCommentPress = onCommentPress,
                onCommentSelected = onCommentSelected,
                isSelected = isSelected)
            setMoreText(totalLeft, comment)
            totalLeft += 15
        }
        setMoreText(totalLeft, comment)
    }

    private fun setMoreText(totalLeft: Int, comment: Comment) {
        binding.addMore.text = "${itemView.context.getString(R.string.show_more)} ${
                    if(comment.count_comments - totalLeft < 15){
                        binding.addMore.visibility = View.GONE
                        comment.count_comments - totalLeft
                    }else {
                        15
                    }
                } ${itemView.context.getString(R.string.more_comments)}"
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

    private fun addComments(
            isSelected: Pair<Comment?, Int>?,
            comment: Comment,
            repository: Repository,
            viewModelScope: CoroutineScope,
            onRespond: (CommentViewBinding, Comment) -> Unit,
            onCommentSelected: (CommentViewBinding,Comment) -> Unit,
            onCommentPress: (CommentViewBinding, Comment) -> Unit) {
        var key = 1
        val inflayer = LayoutInflater.from(itemView.context)
        viewModelScope.launch {
            repository.getComments(commentId = comment.id, page = 1)
                .onStart {
                    binding.commentList.addView(ProgressBar(itemView.context))
                }
                .catch { binding.commentList.removeViewAt(binding.commentList.size - 1) }
                .collect {
                    binding.commentList.removeViewAt(binding.commentList.size - 1)
                    key++
                    it.data.forEach {
                        val commentViewBinding = CommentViewHolder(inflayer.inflate(R.layout.comment_view, null,false))
                        commentViewBinding.bind(
                            comment = it,
                            repository = repository,
                            viewModelScope = viewModelScope,
                            parentView = binding,
                                parent = comment ,
                            onCommentPress = onCommentPress,
                            onCommentSelected = onCommentSelected,
                            onRespond = onRespond,
                            isSelected = isSelected
                        )
                        binding.commentList.addView(commentViewBinding.binding.root)
                    }
                }
        }

    }


    private fun setSpannableText(heightText: TextView, textID: String, toSpan: String, scrollToParent: () -> Unit = {}, onCommentPress: (CommentViewBinding) -> Unit) {

        val spannable = SpannableString(textID)
        val ss = StyleSpan(Typeface.BOLD)
        val flag = Spannable.SPAN_INCLUSIVE_INCLUSIVE

        fun setPinkSpannable(start: Int, end: Int) {
            val boldSpan = ForegroundColorSpan(getColor(heightText.context, R.color.pink))
            spannable.setSpan(boldSpan, start, end, flag)
            val endTwo: Int = textID.length
            val fc = object : ClickableSpan(){
                override fun onClick(widget: View) {
                    scrollToParent()
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.linkColor = Color.GREEN
                }
            }

            val fc2 = object : ClickableSpan(){
                override fun onClick(widget: View) {
                    onCommentPress(binding)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.linkColor = Color.GREEN
                }
            }
            spannable.setSpan(fc, start, end, flag)
            spannable.setSpan(fc2, end, endTwo, flag)
        }

        val indexStart = textID.indexOf(toSpan)
        val indexEnd = textID.indexOf(toSpan) + toSpan.length

        try {
            val boldSpan = ForegroundColorSpan(getColor(heightText.context, R.color.pink))
            val start: Int = textID.indexOf("#")
            val end: Int = textID.indexOf(" ", startIndex = start)
            spannable.setSpan(boldSpan, start, end, flag)
        }catch (e: Exception){

        }

        try {
            setPinkSpannable(textID.indexOf("@"), textID.indexOf(" ", startIndex = textID.indexOf("@")))
        }catch (e: Exception){
           try {
               setPinkSpannable(textID.indexOf("@"), textID.length)
           }catch (e: Exception){

           }
        }

        spannable.setSpan(ss, indexStart, indexEnd, flag)

        heightText.movementMethod = LinkMovementMethod.getInstance()
        heightText.text = spannable
    }
}
