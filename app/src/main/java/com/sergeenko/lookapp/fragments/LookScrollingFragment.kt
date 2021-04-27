package com.sergeenko.lookapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.adapters.LookAdapter
import com.sergeenko.lookapp.databinding.LookScrollingFragmentBinding
import com.sergeenko.lookapp.viewModels.LookScrollingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

    override fun onResume() {
        super.onResume()
        viewModel.lastPostion?.let { binding.rv.scrollToPosition(it) }
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
        val llm = CustomGridLayoutManager(context, RecyclerView.VERTICAL, false)
        (viewModel.adapter as LookAdapter).disableScroll = {
            llm.setScrollEnabled(it)
        }
        llm.setScrollEnabled(false)
        rv.layoutManager = llm
        rv.adapter = viewModel.collectData()
        binding.rv.post {
            viewModel.setScreenHeight(binding.rv.height)
        }
        if(viewModel.lastPostion == null)
            viewModel.lastPostion = (llm.findFirstVisibleItemPosition() + llm.findLastVisibleItemPosition()) / 2

        val snapHelper = PagerSnapHelper()
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            var allowToScroll = true

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val first: Int = llm.findFirstVisibleItemPosition()
                val last: Int = llm.findLastVisibleItemPosition()
                val newPosition =
                        when {
                            dy > 0 -> (first + last) / 2
                            dy < 0 -> (first + last + 1) / 2
                            else -> viewModel.lastPostion
                        }

                if(viewModel.adapter.isPostOpen(viewModel.lastPostion!!)){
                    //llm.setScrollEnabled(false)
                    snapHelper.attachToRecyclerView(null)
                }else{
                    //llm.setScrollEnabled(true)
                    snapHelper.attachToRecyclerView(binding.rv)
                }

                if (newPosition != viewModel.lastPostion && !isCurrentListOpen(dy)) {
                    if(allowToScroll && recyclerView.isInTouchMode) {
                        allowToScroll = false

                        viewModel.adapter.closeLastView(viewModel.lastPostion!!)

                        viewModel.lastPostion = newPosition
                        //binding.rv.scrollToPosition(newPosition!!)
                        lifecycleScope.launch {
                            delay(100)
                            allowToScroll = true
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        viewModel.collectState()
    }

    private fun isCurrentListOpen(dy: Int): Boolean {
        return try {
            viewModel.adapter.currentList?.get(viewModel.lastPostion!!)?.isPostOpen == true && dy < 0
        }catch (e: Exception){
            false
        }
    }

}

