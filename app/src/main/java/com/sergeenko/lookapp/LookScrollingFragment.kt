package com.sergeenko.lookapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
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
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = viewModel.collectData()
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv)
        viewModel.collectState()
    }

}