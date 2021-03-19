package com.sergeenko.lookapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sergeenko.lookapp.models.Code

class CountryCodeAdapter : PagedListAdapter<Code, CountryCodeViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: CountryCodeViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) {
            currentList?.filter { countryCode -> countryCode != null && countryCode.isSelected && countryCode.id != it.id }?.forEach { prevSelected->
                        prevSelected.isSelected = false
                        notifyItemChanged(currentList!!.indexOf(prevSelected))
                    }
            notifyItemChanged(position)
        }
        }
    }

    override fun getItemCount(): Int {
        return currentList?.size ?: 0
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Code>() {
            override fun areItemsTheSame(oldConcert: Code,
                                         newConcert: Code
            ) = oldConcert.id == newConcert.id

            override fun areContentsTheSame(oldConcert: Code,
                                            newConcert: Code
            ) = oldConcert == newConcert
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryCodeViewHolder {
        return CountryCodeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.country_code_view, parent, false)
        )
    }
}

