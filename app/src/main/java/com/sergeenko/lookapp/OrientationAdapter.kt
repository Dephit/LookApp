package com.sergeenko.lookapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

sealed class RotationMode{
    object RotationZ : RotationMode()
    object RotationX : RotationMode()
    object RotationY : RotationMode()
    object None : RotationMode()
}

class OrientationAdapter : RecyclerView.Adapter<OrientationViewHolder>() {

    var list = List(10000) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrientationViewHolder {
        return OrientationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.orientation_view, null, false))
    }

    override fun onBindViewHolder(holder: OrientationViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return list.size
    }

}
