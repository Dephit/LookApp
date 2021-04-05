package com.sergeenko.lookapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class OrientationFragment : Fragment() {

    companion object {
        fun newInstance() = OrientationFragment()
    }

    private lateinit var viewModel: OrientationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.orientation_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrientationViewModel::class.java)
        val newLocalBroadcastManager = LocalBroadcastManager.getInstance(requireContext())
        val popupdataIntent = Intent ("popupdata")

        popupdataIntent.putExtra ("popupdata", bundleOf(
            "photo" to "photo"
        ));
        newLocalBroadcastManager.sendBroadcast(popupdataIntent)
    }

}