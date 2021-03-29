package com.sergeenko.lookapp

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEachIndexed
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.FiltersFragmentBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class FiltersFragment : BaseFragment<FiltersFragmentBinding>() {

    override val viewModel: FiltersViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): FiltersFragmentBinding = FiltersFragmentBinding.inflate(inflater)

    private fun setRV(fileList: List<File>) {
        val adapter = viewModel.adapter
        adapter.setList(_fileList = fileList)
        binding.rv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rv.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rv)
    }

    override fun setListeners() {
        withBinding {
            val file = arguments?.getSerializable("files") as List<File>

            currentItemList.forEachIndexed { index, view ->
                if(index < file.size)
                    view.visibility = View.VISIBLE
                else
                    view.visibility = View.GONE
            }
            setRV(file)
            /*binding.previewImage.visibility = View.GONE
            binding.progressBar4.visibility = View.VISIBLE*/
            /*Picasso.get()
                    .load(file[0])
                    .noPlaceholder()
                    .into(binding.previewImage, object : Callback {
                        override fun onSuccess() {
                            binding.previewImage.visibility = View.VISIBLE
                            binding.progressBar4.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {

                        }

                    })*/
        }
    }

}