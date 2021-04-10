package com.sergeenko.lookapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.databinding.AdditionalActionsCommentLayoutBinding
import com.sergeenko.lookapp.databinding.CommentsFragmentBinding
import com.sergeenko.lookapp.models.Comment
import com.sergeenko.lookapp.models.Look
import com.sergeenko.lookapp.viewModels.CommentsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CommentsFragment : BaseFragment<CommentsFragmentBinding>() {

    override val viewModel: CommentsViewModel by viewModels()

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
                if(obj > 0){
                    binding.commentsView.scrollToPosition(obj)
                }
            }
            is Pair<*, *> -> {
                (binding.commentInput.editText as ZanyDoubleText).setPrevView(obj.first.toString())
                setAnswerToSelectedComment(obj.first as String)
                //binding.commentsView.smoothScrollToPosition(obj.second as Int)
                showComments()
            }
            is Comment -> {
                showCommentEdit()
            }
            null -> {
                showComments()
                binding.commentInput.editText?.setText("")
            }
        }
    }

    private fun showCommentEdit() {
        binding.toolbar.visibility = View.GONE
        binding.toolbarEditTitle.visibility = View.VISIBLE
        binding.commentSection.visibility = View.GONE
    }

    private fun showComments() {
        binding.toolbar.visibility = View.VISIBLE
        binding.toolbarEditTitle.visibility = View.GONE
        binding.commentSection.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun setAnswerToSelectedComment(obj: String) {
        binding.commentInput.editText!!.setText("$obj ")
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
            toolbarEdit.menu.findItem(R.id.report).setOnMenuItemClickListener {
                lifecycleScope.launch(IO) {
                    val isMyComment = viewModel.isMyPost()
                    val isMyAnswer = viewModel.isMyAnswer()
                    withContext(Main){
                        showAdditionalActions(toolbar, toolbar.x + toolbar.width / 2,
                                isYourComment = isMyComment,
                                isYorsAnswer = isMyAnswer,
                                onClaim = viewModel::claim,
                                onDelete = viewModel::deleteComment)
                    }
                }
                return@setOnMenuItemClickListener true
            }

            toolbarEdit.setNavigationOnClickListener {
                showComments()
                viewModel.clearSelection()
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
            var lastLength = 0
            var canDoDoubleTap = 0
            commentInput.editText?.addTextChangedListener {
                if(lastLength > it!!.length){
                    if(canDoDoubleTap == 1 && it.toString() <= viewModel.userText) {
                        canDoDoubleTap = 0
                        commentInput.editText?.setText("")
                        viewModel.clearSelection()
                    }
                    canDoDoubleTap++
                    Handler(Looper.myLooper()!!).postDelayed({
                        canDoDoubleTap = 0
                    }, 2000)
                }
                lastLength = it.length

                if(it.toString() != "${viewModel.userText} " && it.toString() != viewModel.userText)
                    sendButton.isEnabled = it.isEmpty() != true
                else {
                    sendButton.isEnabled = false
                }
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

fun showAdditionalActions(
        view: View,
        x: Float,
        isYourComment: Boolean,
        isYorsAnswer: Boolean,
        onClaim: (String) -> Unit,
        onDelete: () -> Unit,
) {
    val customLayout = AdditionalActionsCommentLayoutBinding.inflate(LayoutInflater.from(view.context))

    customLayout.deleteConfirm.visibility = View.GONE
    if(isYourComment || isYorsAnswer){
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainMyPostDetailed.visibility = View.VISIBLE
        if(isYourComment){
            customLayout.claim.visibility = View.GONE
        }else{
            customLayout.claim.visibility = View.VISIBLE
        }
    }else {
        customLayout.complainDetailed.visibility = View.VISIBLE
    }


    // create an alert builder
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val focusable = true // lets taps outside the popup also dismiss it

    val window = PopupWindow(customLayout.root, width, height, focusable)
    window.isOutsideTouchable = true

    customLayout.claim.setOnClickListener {
        customLayout.complainDetailed.visibility = View.VISIBLE
        customLayout.complainMyPostDetailed.visibility = View.GONE
        customLayout.deleteConfirm.visibility = View.GONE
    }

    customLayout.no.setOnClickListener {
        window.dismiss()
    }

    customLayout.yes.setOnClickListener {
        onDelete()
        window.dismiss()
    }

    customLayout.deleteComment.setOnClickListener {
        customLayout.deleteConfirm.visibility = View.VISIBLE
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainMyPostDetailed.visibility = View.GONE
    }

    customLayout.complainSend.setOnClickListener {
        window.dismiss()
    }

    customLayout.spam.setOnClickListener {
        onClaim("Спам")
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
    }

    customLayout.shockContent.setOnClickListener {
        onClaim("Шокирующий контент")
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
        customLayout.deleteConfirm.visibility = View.GONE
    }

    window.contentView = customLayout.root
    window.showAtLocation(view, Gravity.TOP, x.toInt(), 0)
}

class ZanyDoubleText : TextInputEditText {

    var textToDel: String = ""

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
            context!!,
            attrs,
            defStyle
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?) : super(context!!) {}

    fun focusPrev() {
        setText("")
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return ZanyInputConnection(
                super.onCreateInputConnection(outAttrs),
                true
        )
    }

    fun setPrevView(_textToDel: String) {
        textToDel = _textToDel
    }



    private inner class ZanyInputConnection(target: InputConnection?, mutable: Boolean) :
            InputConnectionWrapper(target, mutable) {

        override fun sendKeyEvent(event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_DEL) {
                //Toast.makeText(context, "sadsd", Toast.LENGTH_SHORT).show()
            }
            return super.sendKeyEvent(event)
        }
    }
}