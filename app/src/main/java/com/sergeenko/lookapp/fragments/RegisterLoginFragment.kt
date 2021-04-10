package com.sergeenko.lookapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.databinding.RegisterLoginFragmentBinding
import com.sergeenko.lookapp.viewModels.RegisterLoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class RegisterLoginFragment : BaseFragment<RegisterLoginFragmentBinding>() {

    @FlowPreview
    @ExperimentalCoroutinesApi
    override val viewModel: RegisterLoginViewModel by viewModels()

    override fun manageLoading(b: Boolean) {
        if(b){
            binding.progressBar.visibility = View.VISIBLE
            binding.next.isEnabled = false
        }else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun <T> manageSuccess(obj: T?) {
        if(binding.editNickname.editText.toString().isNotEmpty()) {
            binding.next.isEnabled = true
            hidePhoneError()
        }
    }

    override fun <T> manageError(error: T?) {
        if(error is Int){
            showError(getString(error))
        }
        super.manageError(error)
    }


    override fun restoreState(savedInstanceState: Bundle?) {
        withBinding {
            editNickname.editText?.setText(viewModel.email)
        }
    }

    private fun showError(string: String) {
        binding.editNickname.editText?.isActivated = true
        binding.errorText.text = string
        binding.errorText.visibility = View.VISIBLE
        binding.next.isEnabled = false
    }

    private fun hidePhoneError() {
        binding.editNickname.editText?.isActivated = false
        binding.errorText.visibility = View.GONE
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun setListeners() {
        with(binding){
            binding.next.isEnabled = false
            editNickname.editText?.addTextChangedListener {
                if(it?.isNotEmpty() == true){
                    editNickname.hint = getString(R.string.login)
                }else {
                    editNickname.hint = getString(R.string.enter_your_nickname)
                    binding.next.isEnabled = false
                }
                hidePhoneError()
                viewModel.checkNickName(it.toString())
            }

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            next.setOnClickListener {
                findNavController().navigate(R.id.action_registerLoginFragment_to_registrationStepOneFragment, bundleOf("email" to viewModel.email))
            }
        }
    }

    override fun bind(inflater: LayoutInflater): RegisterLoginFragmentBinding {
        return RegisterLoginFragmentBinding.inflate(inflater)
    }

}