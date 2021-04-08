package com.sergeenko.lookapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ColorAdapter(private val function: (String) -> Unit) : RecyclerView.Adapter<ColorViewHolder>() {

    private var backgroundColor: String? = null

    val list: List<String> = listOf(
            "#1A1717",
            "#F6F6F6",
            "#FF207A",
            "#EF4898",
            "#EB5757",
            "#F2994A",
            "#F2C94C",
            "#219653",
            "#27AE60",
            "#6FCF97",
            "#2F80ED",
            "#2D9CDB",
            "#56CCF2",
            "#DA277D",
            "#DA277D",
            "#BB6BD9"
            )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.color_view, null, false))
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(list[position], isSelected = list[position] == backgroundColor) {
            function(it)
            updateSelectedColor(it)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateSelectedColor(backgroundColor: String?) {
        this.backgroundColor = backgroundColor
        notifyDataSetChanged()
    }

}
