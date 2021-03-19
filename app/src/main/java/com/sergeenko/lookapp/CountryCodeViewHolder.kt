package com.sergeenko.lookapp

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.CountryCodeViewBinding
import com.sergeenko.lookapp.models.Code

class CountryCodeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    @SuppressLint("SetTextI18n")
    inline fun bind(countryCode: Code, crossinline function: (Code) -> Unit){
        with(CountryCodeViewBinding.bind(itemView)){
            codeText.text = "${countryCode.dialCode} (${countryCode.name_ru})"
            checkImg.isActivated = countryCode.isSelected
            divider2.visibility = if(countryCode.name_ru == "Россия") View.VISIBLE else View.GONE
            root.setOnClickListener {
                countryCode.isSelected = true
                function(countryCode)
            }
        }
    }
}
