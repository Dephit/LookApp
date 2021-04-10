package com.sergeenko.lookapp.fragments

import android.graphics.Bitmap
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.viewModels.FinaLookScreenViewModel
import com.sergeenko.lookapp.adapters.FinalLookImageAdapter
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