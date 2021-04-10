package com.sergeenko.lookapp.fragments

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.github.razir.progressbutton.hideProgress
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.databinding.PhoneCodeEnterFragmentBinding
import com.sergeenko.lookapp.models.SocialResponse
import com.sergeenko.lookapp.viewModels.PhoneCodeEnterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PhoneCodeEnterFragment : BaseFragment<PhoneCodeEnterFragmentBinding>() {

    override val viewModel: PhoneCodeEnterViewModel by navGraphViewModels(R.id.main_navigation) {
        defaultViewModelProviderFactory
    }

    override fun <T> manageSuccess(obj: T?) {
        if(obj is SocialResponse) {
            startMainActivity(obj, R.id.action_phoneCodeEnterFragment_to_registerLoginFragment)
            binding.enter.hideProgress(getString(R.string.confirm))
            viewModel.restoreState()
        }
    }

    override fun setListeners() {
        with(binding){
            val phoneRaw = arguments?.getString("phone")
            setPhoneSpannable(phoneRaw)
            val phone = phoneRaw?.replace(("[()-]").toRegex(), "")

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            initTimer(phoneRaw)

            code1.setPhoneFocusChangeListener(code2.editText, null)
            code2.setPhoneFocusChangeListener(code3.editText, code1.editText)
            code3.setPhoneFocusChangeListener(code4.editText, code2.editText)
            code4.setPhoneFocusChangeListener(code5.editText, code3.editText)
            code5.setPhoneFocusChangeListener(null, code4.editText)

            resendCode.setOnClickListener {
                if(resendCode.text == getString(R.string.send_again)) {
                    viewModel.sendCodeAgain(phone)
                    initTimer(phoneRaw)
                }
            }

            enter.setOnClickListener {
                enter.showWhiteProgress()
                if (phone != null) {
                    viewModel.checkCode(getCode(), phone)
                }
            }
        }
    }

    override fun manageError(bool: Boolean) {
        if(bool){
            binding.enter.hideProgress(getString(R.string.confirm))
        }
        super.manageError(bool)
    }

    override fun <T> manageError(error: T?) {
        if(error is Int){
            showToast(getString(error))
        }
        super.manageError(error)
    }

    private fun setPhoneSpannable(phone: String?, color: Int? = null) {
        if (phone != null) {
            setSpannableText(
                binding.phoneText,
                "${getString(R.string.on_phone_number)} $phone ${getString(R.string.was_send_code)}",
                phone,
                color
            )
        }
    }

    private fun getCode(): String {
        return binding.code1.editText?.text.toString() + binding.code2.editText?.text.toString() +
                binding.code3.editText?.text.toString() + binding.code4.editText?.text.toString() + binding.code5.editText?.text.toString()
    }

    private fun initTimer(phone: String?) {
        //setPhoneSpannable(phone, R.color.pink)
        binding.resendCode.setTextColor(getColor(requireContext(), R.color.black_20))
        lifecycleScope.launch {
            var count = 30
            while (count > 0){
                val string = getString(R.string.left)
                val string2 = " $count ${getString(R.string.seconds)}"
                setSpannableText(binding.resendCode, string + string2, string2, R.color.black_100)
                delay(1000)
                count--
            }
            binding.resendCode.setTextColor(getColor(requireContext(), R.color.pink_pressed))
          //  setPhoneSpannable(phone)
            binding.resendCode.text = getString(R.string.send_again)
        }
    }

    private fun TextInputLayout.setPhoneFocusChangeListener(viewNext: View?, viewBefore: EditText?) {
        editText?.setOnFocusChangeListener { _, hasFocus ->
            isSelected = hasFocus
            hint = if(hasFocus) "" else if(editText?.text?.isNotEmpty() == true ) "" else "0"
        }


        (editText as ZanyEditText).setPrevView(viewBefore)
        editText?.addTextChangedListener {
            hint = ""
            checkCodeEntered()
            if(it?.isNotEmpty() == true)
                viewNext?.requestFocusFromTouch() ?: hideKeyboard()
        }
    }

    private fun checkCodeEntered() {
        if(getCode().length == 5){
            binding.enter.isEnabled = true
        }
    }

    private fun setSpannableText(
        heightText: TextView,
        textID: String,
        toSpan: String,
        color: Int? = null
    ) {
        val spannable = SpannableString(textID)

        val ss = StyleSpan(Typeface.BOLD)


        val indexStart = textID.indexOf(toSpan)
        val indexEnd = textID.indexOf(toSpan) + toSpan.length

        spannable.setSpan(ss, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        if(color != null) {
            val fc = ForegroundColorSpan(getColor(requireContext(), color))
            spannable.setSpan(fc, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        heightText.text = spannable
    }

    override fun bind(inflater: LayoutInflater): PhoneCodeEnterFragmentBinding {
        return PhoneCodeEnterFragmentBinding.inflate(inflater)
    }
}

class ZanyEditText : TextInputEditText {

     var viewBefore: EditText? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?) : super(context!!) {}

    fun focusPrev() {
        viewBefore?.requestFocusFromTouch()
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return ZanyInputConnection(
            super.onCreateInputConnection(outAttrs),
            true
        )
    }

    fun setPrevView(_viewBefore: EditText?) {
        viewBefore = _viewBefore
    }

    private inner class ZanyInputConnection(target: InputConnection?, mutable: Boolean) :
        InputConnectionWrapper(target, mutable) {
        override fun sendKeyEvent(event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_DEL
            ) {
                if(editableText.isEmpty())
                    focusPrev()
            }
            return super.sendKeyEvent(event)
        }
    }
}