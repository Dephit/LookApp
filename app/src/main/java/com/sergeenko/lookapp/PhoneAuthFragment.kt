package com.sergeenko.lookapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.sergeenko.lookapp.databinding.PhoneAuthFragmentBinding
import com.sergeenko.lookapp.models.Code
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PhoneAuthFragment : BaseFragment<PhoneAuthFragmentBinding>() {

    override val viewModel: PhoneAuthViewModel  by viewModels()

    override fun <T> manageError(error: T?) {
        if (error != null && error is Int){
            showPhoneError(getString(error))
        }else if(error is String){
            showToast(error)
        }
        super.manageError(error)
    }

    override fun restoreState(savedInstanceState: Bundle?) {
        withBinding {
            editPhone.editText?.append(viewModel.phone)

        }
    }
    
    override fun <T> manageSuccess(obj: T?) {
        if(obj is String){
            hidePhoneError()
            binding.enter.hideProgress(R.string.enter)
            viewModel.restoreState()
            hideKeyboard()
            try {
                findNavController().navigate(R.id.action_phoneAuthFragment_to_phoneCodeEnterFragment, bundleOf("phone" to "${binding.choseCode.text}${binding.editPhone.editText?.text}"))
            }catch (e: IllegalArgumentException){

            }
        }
    }

    override fun manageLoading(b: Boolean) {
        if(b){
            binding.enter.showProgress {
                progressColor = getColor(requireContext(), R.color.white)
            }
        }else {
            binding.enter.hideProgress(R.string.enter)
        }
    }

    override fun setListeners() {
        withBinding{
            setCountryCode()

            binding.enter.isEnabled = false

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            choseCode.setOnClickListener {
                findNavController().navigate(R.id.action_phoneAuthFragment_to_choseCodeFragment)
            }

            enter.setOnClickListener {
                viewModel.authByPhone(editPhone.editText?.text)
            }

            editPhone.editText?.setOnFocusChangeListener { _, _ ->
                setPhoneHint()
            }

            editPhone.editText?.addTextChangedListener {
                updatePhone(it.toString())
            }
            if(viewModel.phone.isNotEmpty()){
                binding.enter.isEnabled = true
            }
        }
    }

    private fun updatePhone(phone: String) {
        hidePhoneError()
        binding.enter.isEnabled = true
        setPhoneHint()
        viewModel.phone = phone
    }

    private fun setPhoneHint() {
        binding.editPhone.editText?.hint = if(binding.editPhone.editText?.text?.isEmpty() == true && binding.editPhone.editText?.isFocused == true) viewModel.getFullHint() else ""
    }

    private fun setCountryCode() {
        lifecycleScope.launchWhenResumed {
            var code: Code?
            withContext(IO){
                code = viewModel.getCurrentCode()
            }
            if(code != null){
                binding.choseCode.text = code!!.dialCode
                binding.editPhone.editText?.addTextChangedListener(NumberTextWatcher(viewModel.getHint()!!))
                setPhoneHint()
            }
        }
    }

    private fun showPhoneError(string: String) {
        binding.editPhone.editText?.isActivated = true
        binding.errorText.text = string
        binding.errorText.visibility = View.VISIBLE
        binding.enter.isEnabled = false
    }

    private fun hidePhoneError() {
        binding.editPhone.editText?.isActivated = false
        binding.errorText.visibility = View.GONE
        binding.enter.isEnabled = true
    }

    override fun bind(inflater: LayoutInflater): PhoneAuthFragmentBinding {
        return PhoneAuthFragmentBinding.inflate(inflater)
    }
}

