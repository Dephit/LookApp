package com.sergeenko.lookapp

import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.FinaLookScreenFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FinaLookScreenFragment : BaseFragment<FinaLookScreenFragmentBinding>() {


     override val viewModel: FinaLookScreenViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): FinaLookScreenFragmentBinding = FinaLookScreenFragmentBinding.inflate(inflater)

    override fun setListeners() {
        withBinding {
            setRv()
        }
    }

    private fun setRv() {
        withBinding {
            val list = arguments?.getParcelableArrayList("filters") ?: listOf<Bitmap?>()
            finalLookRv.adapter = FinalLookImageAdapter().also {
                it.updateList(list)
            }
            finalLookRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
    }
}