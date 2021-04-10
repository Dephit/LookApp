package com.sergeenko.lookapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.addCallback
import androidx.navigation.navGraphViewModels
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.databinding.PostTapeFragmentBinding
import com.sergeenko.lookapp.viewModels.PostTapeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostTapeFragment : BaseFragment<PostTapeFragmentBinding>() {

    override val viewModel: PostTapeViewModel by navGraphViewModels(R.id.main_navigation) {
        defaultViewModelProviderFactory
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {

        }
    }


    override fun bind(inflater: LayoutInflater): PostTapeFragmentBinding {
        return PostTapeFragmentBinding.inflate(inflater)
    }



}