package com.sergeenko.lookapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.viewHolders.OrientationViewHolder
import com.sergeenko.lookapp.R
import javax.inject.Inject

sealed class RotationMode{
    object RotationZ : RotationMode()
    object RotationX : RotationMode()
    object RotationY : RotationMode()
    object None : RotationMode()
}

class OrientationAdapter @Inject constructor() : RecyclerView.Adapter<OrientationViewHolder>() {

    var list = List(20) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrientationViewHolder {
        return OrientationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.orientation_view, null, false))
    }

    override fun onBindViewHolder(holder: OrientationViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

}
