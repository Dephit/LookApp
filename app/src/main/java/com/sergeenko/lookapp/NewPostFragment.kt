package com.sergeenko.lookapp

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.AddLinkPopUpBinding
import com.sergeenko.lookapp.databinding.MarkUserPopUpBinding
import com.sergeenko.lookapp.databinding.NewPostFragmentBinding
import com.vanniktech.emoji.EmojiPopup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPostFragment : WhiteStatusBarFragment<NewPostFragmentBinding>() {

    override val statusBarColor: Int = Color.WHITE

     override val viewModel: NewPostViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): NewPostFragmentBinding {
        return NewPostFragmentBinding.inflate(inflater)
    }

    override fun setListeners() {
        withBinding {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            val emojiPopup = EmojiPopup.Builder.fromRootView(root).build(bodyTextInput)

            emojyIcon.setOnClickListener {
                emojiPopup.toggle()
                emojyIcon.isActivated = !emojyIcon.isActivated
            }

            addLookIcon.setOnClickListener {
                findNavController().navigate(R.id.action_global_newLookFragment)
            }

            markUserIcon.setOnClickListener {
                bodyTextInput.editableText.append(" @")
                val boldSpan = ForegroundColorSpan(getColor(requireContext(), R.color.pink))
                val start: Int = bodyTextInput.editableText.toString().length - 1
                val end: Int = bodyTextInput.editableText.toString().length
                val flag = Spannable.SPAN_INCLUSIVE_INCLUSIVE
                bodyTextInput.editableText.setSpan(boldSpan, start, end, flag)
                showMarkUserPopUp(it, List(10){"asdsd $it"}) { nick ->
                    val str = "$nick "
                    bodyTextInput.editableText.append(str)
                    val boldSpan = ForegroundColorSpan(getColor(requireContext(), R.color.pink))
                    val start: Int = bodyTextInput.editableText.toString().length - str.length
                    val end: Int = bodyTextInput.editableText.toString().length
                    val flag = Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    bodyTextInput.editableText.setSpan(boldSpan, start, end, flag)
                    val boldSpan2 = ForegroundColorSpan(getColor(requireContext(), R.color.black))
                    bodyTextInput.editableText.setSpan(boldSpan2, end, end, flag)
                }
            }

            bodyTextInput.addTextChangedListener {
                if(it.toString().last() == ' '){
                    val end: Int = bodyTextInput.editableText.toString().length
                    val flag = Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    val boldSpan2 = ForegroundColorSpan(getColor(requireContext(), R.color.black))
                    bodyTextInput.editableText.setSpan(boldSpan2, end, end, flag)
                }
            }

            addLinkIcon.setOnClickListener { view ->
                showAddLinkPopUp(view) {
                    bodyTextInput.editableText.append(it)
                    val boldSpan = ForegroundColorSpan(getColor(requireContext(), R.color.pink))
                    val start: Int = bodyTextInput.editableText.toString().length - it.length
                    val end: Int = bodyTextInput.editableText.toString().length
                    val flag = Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    bodyTextInput.editableText.setSpan(boldSpan, start, end, flag)
                    val boldSpan2 = ForegroundColorSpan(getColor(requireContext(), R.color.black))
                    bodyTextInput.editableText.setSpan(boldSpan2, end, end, flag)
                }
            }

            headerHint.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus)
                    focusHeader()
            }

            headerTextInput.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus)
                    focusHeader()
            }

            setHeaderHintSpannableText()
            setPhotoHintSpannableText()

            binding.headerTextInput.editText?.setOnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    binding.headerHint.visibility = View.GONE
                }else {
                    if(binding.headerTextInput.editText?.text?.isEmpty() == true)
                        binding.headerHint.visibility = View.VISIBLE
                    else{
                        binding.headerHint.visibility = View.GONE
                    }
                }

            }
        }
    }

    private fun setPhotoHintSpannableText() {
        val text = getString(R.string.new_post_phonto_hint)
        val spannable = SpannableString(text)

        val indexStarStart = text.indexOf("*")
        val indexStarEnd = text.indexOf("*") + "*".length

        val fc = ForegroundColorSpan(getColor(requireContext(), R.color.pink))
        spannable.setSpan(fc, indexStarStart, indexStarEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.photoHint.text = spannable
    }

    private fun setHeaderHintSpannableText() {
        val text = getString(R.string.new_post_header_hint)
        val spannable = SpannableString(text)

        val indexStarStart = text.indexOf("*")
        val indexStarEnd = text.indexOf("*") + "*".length

        val indexSecondStart = text.indexOf("\n")
        val indexSecondEnd = text.length

        val fc = ForegroundColorSpan(getColor(requireContext(), R.color.pink))
        val fc2 = ForegroundColorSpan(getColor(requireContext(), R.color.black_40))
        val rss = RelativeSizeSpan(0.7f)

        spannable.setSpan(fc, indexStarStart, indexStarEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(fc2, indexSecondStart, indexSecondEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(rss, indexSecondStart, indexSecondEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.headerHint  .text = spannable
    }

    private fun focusHeader() {
        binding.headerTextInput.editText?.requestFocus()
    }


}


inline fun showAddLinkPopUp(view: View, crossinline onSave: (String) -> Unit) {
    val customLayout = AddLinkPopUpBinding.inflate(LayoutInflater.from(view.context))

    // create an alert builder
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val focusable = true // lets taps outside the popup also dismiss it

    val window = PopupWindow(customLayout.root, width, height, focusable)
    window.isOutsideTouchable = true

    customLayout.cancel.setOnClickListener {
        window.dismiss()
    }

    customLayout.save.setOnClickListener {
        onSave(" ${customLayout.linkInputLayout.editText?.text.toString()} ")
        window.dismiss()
    }

    window.contentView = customLayout.root
    window.showAtLocation(view, Gravity.CENTER, 0, 0)
}

fun showMarkUserPopUp(view: View, userList: List<String>, onSave: (String) -> Unit) {
    val customLayout = MarkUserPopUpBinding.inflate(LayoutInflater.from(view.context))

    // create an alert builder
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val focusable = true // lets taps outside the popup also dismiss it

    val window = PopupWindow(customLayout.root, width, height, focusable)
    window.isOutsideTouchable = true

    val adapter = MarkUserAdapter{
        onSave(it)
        window.dismiss()
    }

    adapter.setList(userList)
    customLayout.userList.layoutManager = LinearLayoutManager(view.context)
    customLayout.userList.adapter = adapter

    window.contentView = customLayout.root
    window.showAtLocation(view, Gravity.CENTER, 0, 0)
}

class MarkUserAdapter(private val onSave: (String) -> Unit) : RecyclerView.Adapter<MarkUserViewHolder>(){

    var userList: List<String> = listOf()

    fun setList(newUserList: List<String>){
        userList = newUserList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkUserViewHolder {
        return MarkUserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mark_user_element, null, false))
    }

    override fun onBindViewHolder(holder: MarkUserViewHolder, position: Int) {
        holder.bind(userList[position]){
            onSave(it)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}

