package com.sergeenko.lookapp

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.navGraphViewModels
import com.sergeenko.lookapp.databinding.PostTapeFragmentBinding
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