package com.sergeenko.lookapp

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeenko.lookapp.databinding.RegistrationStepThreeFragmentBinding
import com.super_rabbit.wheel_picker.OnValueChangeListener
import com.super_rabbit.wheel_picker.WheelPicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationStepThreeFragment : BaseFragment<RegistrationStepThreeFragmentBinding>() {

    override val viewModel: RegistrationStepThreeViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): RegistrationStepThreeFragmentBinding {
        return RegistrationStepThreeFragmentBinding.inflate(inflater)
    }

    override fun restoreState(savedInstanceState: Bundle?) {
        withBinding {
            weightPicker.scrollToValue(viewModel.weight ?: "66")
            //next.isEnabled = viewModel.weight != null
        }
    }

    override fun setListeners(){
        withBinding {
            skipText.setOnClickListener {
                toNextStep()
            }

            weightPicker.setSelectedTextColor(R.color.black_100)
            weightPicker.setOnValueChangeListener(object : OnValueChangeListener {
                override fun onValueChange(picker: WheelPicker, oldVal: String, newVal: String) {
                    next.isEnabled = true
                    viewModel.weight(newVal)
                }

            })

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            next.setOnClickListener {
                toNextStep(viewModel.weight)
            }
        }
    }

    private fun toNextStep(weight: String? = null) {
        findNavController().navigate(R.id.action_registrationStepThreeFragment_to_registrationStepFourFragment,
                bundleOf(
                        "email" to arguments?.getString("email"),
                        "gender" to arguments?.getString("gender"),
                        "height" to arguments?.getString("height"),
                        "weight" to weight
                )
        )
    }


}