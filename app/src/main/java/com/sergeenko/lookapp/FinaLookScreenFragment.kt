package com.sergeenko.lookapp

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FinaLookScreenFragment : Fragment() {

    companion object {
        fun newInstance() = FinaLookScreenFragment()
    }

    private lateinit var viewModel: FinaLookScreenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fina_look_screen_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FinaLookScreenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}