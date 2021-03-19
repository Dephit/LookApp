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
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.CommentViewBinding
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class CommentViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

    val binding = CommentViewBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bind(comment: Comment, onCommentPress: (Comment) -> Unit, onRespond: (CommentsListAdapter, Comment) -> Unit, level: Boolean = false, scrollToParent: (Int)-> Unit = {}, isSelected: Int){
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

            addComments(selectedComment = isSelected, comment.comments, onCommentPress, onRespond, {
                scrollToParent(comment.id)
            })

            if(level){
                binding.comment.setPadding(itemView.context.resources.getDimension(R.dimen._40sdp).roundToInt(), 0,0,0)
            }

            val text = "${comment.user.username} ${comment.text}"
            try {
                setSpannableText(commentText, text, comment.user.username, scrollToParent = {
                    scrollToParent(comment.id)
                })
            }catch (e: Exception){

            }

            avatar.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.action_commentsFragment_to_profileFragment)
            }

            respondText.setOnClickListener {
                onRespond(commentsList.adapter as CommentsListAdapter, comment)
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


    private fun addComments(selectedComment: Int, comments: List<Comment>, onCommentPress: (Comment) -> Unit,  onRespond: (CommentsListAdapter, Comment) -> Unit,  scrollToParent: (Int)-> Unit = {}) {
        val llm = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
        binding.commentsList.layoutManager = llm
        binding.commentsList.adapter = CommentsListAdapter(
                selectedComment = selectedComment,
                list = comments,
                onCommentPress = { onCommentPress(it) },
                onRespond = onRespond,
                scrollToParent = scrollToParent
        )
    }


    private fun setSpannableText(heightText: TextView, textID: String, toSpan: String, scrollToParent: ()-> Unit = {}) {
        val spannable = SpannableString(textID)
        val ss = StyleSpan(Typeface.BOLD)

        val indexStart = textID.indexOf(toSpan)
        val indexEnd = textID.indexOf(toSpan) + toSpan.length

        val flag = Spannable.SPAN_INCLUSIVE_INCLUSIVE



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
