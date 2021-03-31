package com.sergeenko.lookapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.LookScrollingFragmentBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LookScrollingFragment : BaseFragment<LookScrollingFragmentBinding>() {

    override val viewModel: LookScrollingViewModel by navGraphViewModels(R.id.menu_navigation) {
        defaultViewModelProviderFactory
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setStatusBar()
    }

    private fun setStatusBar() {
        val w: Window = requireActivity().window
        w.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        w.statusBarColor = Color.TRANSPARENT
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            w.decorView.systemUiVisibility = w.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //set status text  light
        }
    }

    override fun bind(inflater: LayoutInflater): LookScrollingFragmentBinding {
        return LookScrollingFragmentBinding.inflate(inflater)
    }

    override fun setListeners() {
        withBinding {
            setRV(rv)
        }
    }

    private fun setRV(rv: RecyclerView) {
        val llm = LinearLayoutManager(context)
        rv.layoutManager = llm
        rv.adapter = viewModel.collectData()
        binding.rv.post {
            viewModel.setScreenHeight(binding.rv.height)
        }
        /*val snapHelper = MySnapHelper()
        snapHelper.attachToRecyclerView(rv)*/
        var lastPostion = 0
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val first: Int = llm.findFirstVisibleItemPosition()
                val last: Int = llm.findLastVisibleItemPosition()
                val newPosition = (first + last) / 2
                if(newPosition != lastPostion){
                    lastPostion = newPosition
                    binding.rv.smoothScrollToPosition(newPosition)
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        viewModel.collectState()
    }

}

class MySnapHelper(): LinearSnapHelper() {


}