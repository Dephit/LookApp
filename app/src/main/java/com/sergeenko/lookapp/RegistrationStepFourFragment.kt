 package com.sergeenko.lookapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sergeenko.lookapp.databinding.RegistrationStepFourFragmentBinding
import com.sergeenko.lookapp.models.SocialResponse
import com.super_rabbit.wheel_picker.OnValueChangeListener
import com.super_rabbit.wheel_picker.WheelPicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

 @ExperimentalCoroutinesApi
 @AndroidEntryPoint
class RegistrationStepFourFragment : BaseFragment<RegistrationStepFourFragmentBinding>() {

    override val viewModel: RegistrationStepFourViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): RegistrationStepFourFragmentBinding {
        return RegistrationStepFourFragmentBinding.inflate(inflater)
    }

    override fun setListeners(){
        withBinding {

            skipText.setOnClickListener {
                toNextStep()
            }

            hipsPicker.setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    viewModel.hips(newVal)
                }

            })

            waistPicker.setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    viewModel.waist(newVal)
                }

            })

            chestPicker.setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    viewModel.chest(newVal)
                }

            })

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            iDontKnowText.setOnClickListener {
                toNextStep()
            }

            next.setOnClickListener {
                toNextStep(chest = viewModel.chest, waist = viewModel.waist, hips = viewModel.hips)
            }
        }
    }

     override fun restoreState(savedInstanceState: Bundle?) {
         withBinding {
             hipsPicker.scrollToValue(viewModel.hips ?: "66")
             chestPicker.scrollToValue(viewModel.chest ?: "66")
             waistPicker.scrollToValue(viewModel.waist ?: "66")
             viewModel.checkField()
         }
     }

    override fun <T> manageSuccess(obj: T?) {
        if(obj is Boolean){
            binding.next.isEnabled = true
        }else if(obj is SocialResponse){
            if(BuildConfig.IS_ALWAYS_REG){
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.fetchBrands()
                    startActivity(Intent(context, MenuActivity::class.java))
                    requireActivity().finish()
                }
            }else{
                startMainActivity(obj, R.id.action_authFragment_to_registerLoginFragment)
            }
        }

    }

     private fun toNextStep(chest: String? = null, waist: String? = null, hips: String? = null) {
        viewModel.updateProfile(bundleOf(
                "email" to arguments?.getString("email"),
                "gender" to arguments?.getString("gender"),
                "height" to arguments?.getString("height"),
                "weight" to arguments?.getString("weight"),
                "chest" to chest,
                "waist" to waist,
                "hips" to hips
        ))
    }
}