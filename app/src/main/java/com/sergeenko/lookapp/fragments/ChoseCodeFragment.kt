package com.sergeenko.lookapp.fragments

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.razir.progressbutton.hideProgress
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.databinding.ChoseCodeFragmentBinding
import com.sergeenko.lookapp.viewModels.ChoseCodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ChoseCodeFragment : BaseFragment<ChoseCodeFragmentBinding>() {

    override val viewModel: ChoseCodeViewModel by viewModels()

    override fun manageLoading(b: Boolean) {
        if(b){
            binding.choseCode.showWhiteProgress()
        }else {
            binding.choseCode.hideProgress(R.string.chose)
        }
    }

    override fun <T> manageSuccess(obj: T?) {
        findNavController().popBackStack()
    }

    override fun setListeners() {
        with(binding){
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            setRV(rv)
            choseCode.setOnClickListener {
                viewModel.choseCountryCode()
            }
        }
    }

    private fun setRV(rv: RecyclerView) {
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = viewModel.collectData()
    }

    override fun bind(inflater: LayoutInflater): ChoseCodeFragmentBinding {
        return ChoseCodeFragmentBinding.inflate(inflater)
    }


}