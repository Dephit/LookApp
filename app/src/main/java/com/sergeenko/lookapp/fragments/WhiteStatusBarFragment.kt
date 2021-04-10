package com.sergeenko.lookapp.fragments

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.viewbinding.ViewBinding

abstract class WhiteStatusBarFragment<T : ViewBinding>: BaseFragment<T>() {

    abstract  val statusBarColor: Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setStatusBar()
    }

    open fun setStatusBar() {
        val w: Window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //set status text  light
        }
        w.statusBarColor = Color.TRANSPARENT
    }

}