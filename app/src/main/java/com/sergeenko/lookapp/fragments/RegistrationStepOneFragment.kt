package com.sergeenko.lookapp.fragments

import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.databinding.*
import com.sergeenko.lookapp.viewModels.RegistrationStepOneViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationStepOneFragment : BaseFragment<RegistrationStepOneFragmentBinding>() {

    override val viewModel: RegistrationStepOneViewModel by viewModels()

    override fun setListeners() {
        withBinding{
            skipText.setOnClickListener {
                toNextStep()
            }

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            initTab()
            tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.customView?.isSelected = true
                    next.isEnabled = true
                    viewModel.selectSex(tabs.selectedTabPosition)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    tab?.customView?.isSelected = false
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
            next.setOnClickListener {
                toNextStep(viewModel.gender)
            }
        }
    }

    private fun initTab() {
        withBinding {
            val inflater = LayoutInflater.from(context)
            tabs.getTabAt(0)?.customView = LeftTabBinding.inflate(inflater).also { it.tabName.text = getString(R.string.male_sex) }.root
            tabs.getTabAt(1)?.customView = MidTabBinding.inflate(inflater).also { it.tabName.text = getString(R.string.female_sex) }.root
            tabs.getTabAt(2)?.customView = RightTabBinding.inflate(inflater).also { it.tabName.text = getString(R.string.other) }.root
            tabs.getTabAt(3)?.view?.visibility = View.GONE
            tabs.selectTab(tabs.getTabAt(viewModel.genderNum ?: 3))
            next.isEnabled = viewModel.gender != null
        }
    }

    private fun toNextStep(gender: String? = null) {
        findNavController().navigate(R.id.action_registrationStepOneFragment_to_registrationStepTwoFragment,
                bundleOf(
                        "email" to arguments?.getString("email"),
                        "gender" to gender
                )
        )
    }

    override fun bind(inflater: LayoutInflater): RegistrationStepOneFragmentBinding {
        return RegistrationStepOneFragmentBinding.inflate(inflater)
    }

}