package com.sergeenko.lookapp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeenko.lookapp.databinding.RegistrationStepTwoFragmentBinding
import com.super_rabbit.wheel_picker.OnValueChangeListener
import com.super_rabbit.wheel_picker.WheelPicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationStepTwoFragment : BaseFragment<RegistrationStepTwoFragmentBinding>() {

    override val viewModel: RegistrationStepTwoViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): RegistrationStepTwoFragmentBinding {
        return RegistrationStepTwoFragmentBinding.inflate(inflater)
    }

    override fun restoreState(savedInstanceState: Bundle?) {
        withBinding {
            asd.scrollToValue(viewModel.height ?: "180")
            ///next.isEnabled = viewModel.height != null
        }
    }

    override fun setListeners(){
        withBinding {
            skipText.setOnClickListener {
                toNextStep()
            }

            asd.setSelectedTextColor(R.color.black_100)
            asd.setOnValueChangeListener(object : OnValueChangeListener{
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    next.isEnabled = true
                    viewModel.height(newVal)
                }

            })

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            next.setOnClickListener {
                toNextStep(viewModel.height)
            }            
        }
    }

    private fun toNextStep(height: String? = null) {
        findNavController().navigate(R.id.action_registrationStepTwoFragment_to_registrationStepThreeFragment,
                bundleOf(
                        "email" to arguments?.getString("email"),
                        "gender" to arguments?.getString("gender"),
                        "height" to height
                )
                )
    }

}
