package com.sergeenko.lookapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.AdditionActionsLayoutBinding
import com.sergeenko.lookapp.databinding.AdditionalActionsCommentLayoutBinding
import com.sergeenko.lookapp.databinding.CommentsFragmentBinding
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.stream.StreamSupport

@AndroidEntryPoint
class CommentsFragment() : BaseFragment<CommentsFragmentBinding>() {

    override val viewModel: CommentsViewModel  by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setStatusBar()
    }

    override fun restoreState(savedInstanceState: Bundle?) {
        viewModel.set(arguments?.getParcelable<Look>("look")!!)
    }

    private fun setStatusBar() {
        val w: Window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //set status text  light
        }
        w.statusBarColor = Color.WHITE
    }

    override fun bind(inflater: LayoutInflater): CommentsFragmentBinding {
        return CommentsFragmentBinding.inflate(inflater)
    }

    @SuppressLint("SetTextI18n")
    override fun <T> manageSuccess(obj: T?) {
        when (obj) {
            is Int ->{
                binding.commentsView.scrollToPosition(obj)
            }
            is Pair<*, *> -> {
                setAnswerToSelectedComment(obj.first as String)
                binding.commentsView.smoothScrollToPosition(obj.second as Int)
            }
            is Comment -> {
                binding.toolbarTitle.text = getString(R.string.comment_chosen)
                binding.toolbar.menu.findItem(R.id.report).isVisible = true
                binding.commentSection.visibility = View.GONE
            }
            null -> {
                binding.toolbarTitle.text = getString(R.string.comments)
                binding.toolbar.menu.findItem(R.id.report).isVisible = false
                binding.commentSection.visibility = View.VISIBLE
                binding.commentInput.editText?.setText("")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setAnswerToSelectedComment(obj: String) {
        binding.commentInput.editText?.setText("$obj ")
        val boldSpan = ForegroundColorSpan(getColor(requireContext(), R.color.pink))
        val start = 0
        val end: Int = binding.commentInput.editText?.editableText.toString().length
        val flag = Spannable.SPAN_INCLUSIVE_INCLUSIVE
        binding.commentInput.editText?.editableText?.setSpan(boldSpan, start, end, flag)

        val boldSpan2 = ForegroundColorSpan(getColor(requireContext(), R.color.black))
        binding.commentInput.editText!!.editableText.setSpan(boldSpan2, end, end, flag)
        binding.commentInput.editText!!.requestFocusFromTouch()
        showKeyBoard(binding.commentInput.editText!!)
        binding.commentInput.editText!!.setSelection(binding.commentInput.editText?.editableText.toString().length)
    }


    @ExperimentalCoroutinesApi
    override fun setListeners() {
        withBinding {
            avatar.clipToOutline = true
            toolbar.menu.findItem(R.id.report).isVisible = false
            toolbar.menu.findItem(R.id.report).setOnMenuItemClickListener {
                showAdditionalActions(toolbar, toolbar.x + toolbar.width / 2, viewModel.selectedComment!!)
                return@setOnMenuItemClickListener true
            }

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            sendButton.setOnClickListener {
                val text = commentInput.editText?.text.toString()
                if(text.isNotEmpty()) {
                    hideKeyboard()
                    viewModel.addComment(text)
                }
            }

            commentInput.editText?.addTextChangedListener {
                sendButton.isEnabled = it?.isEmpty() != true
            }

            sendButton.isEnabled = false
            setRV(commentsView)
        }
    }

    private fun setRV(rv: RecyclerView) {
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = viewModel.collectData()
        viewModel.collectState()
    }

}

fun showAdditionalActions(view: View, x: Float, img: Comment) {

    val customLayout = AdditionalActionsCommentLayoutBinding.inflate(LayoutInflater.from(view.context))

    fun checkFavorite(){
        //customLayout.addToFavImg.isActivated = img.isFavorite
    }

    // create an alert builder
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val focusable = true // lets taps outside the popup also dismiss it

    val window = PopupWindow(customLayout.root, width, height, focusable)
    window.isOutsideTouchable = true

    checkFavorite()

    customLayout.complainSend.setOnClickListener {
        window.dismiss()
    }

    customLayout.spam.setOnClickListener {
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
    }

    customLayout.shockContent.setOnClickListener {
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
    }

    window.contentView = customLayout.root
    window.showAtLocation(view, Gravity.TOP, x.toInt(), 0)
}