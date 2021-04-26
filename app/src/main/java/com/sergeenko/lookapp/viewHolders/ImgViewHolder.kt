package com.sergeenko.lookapp.viewHolders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.databinding.LookImgBinding
import com.sergeenko.lookapp.databinding.PriceLayoutBinding
import com.sergeenko.lookapp.models.Image
import com.sergeenko.lookapp.models.Mark
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ImgViewHolder(itemView: View) : LookViewViewHolder(itemView) {

    val binding = LookImgBinding.bind(itemView)

    fun bind(img: Image, onRootPressed: (View) -> Unit) {
        with(binding) {
            loadImg(img)
            setListeners(img, onRootPressed)
        }
    }

    private fun setListeners(img: Image, onRootPressed: (View) -> Unit) {
        with(binding){
            lookImg.setOnClickListener {
                onRootPressed(binding.totalSection)
            }
        }
    }

    private fun formatWithThousandsSeparator(num: Int): String {
        val numAsString = num.toString().reversed()
        var str = ""

        for (i in numAsString.indices) {
            if(i % 3 == 0){
                str += " ${numAsString[i]}"
            }else {
                str += numAsString[i]
            }
        }
        return str.reversed()
    }

    private fun loadImg(img: Image) {
        showDots(img)
        Picasso.get()
            .load(img.url)
            .noPlaceholder()
            .error(R.drawable.background_splash)
            .into(binding.lookImg, object : Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.progressBar.visibility = View.GONE
                    binding.errorText.visibility = View.VISIBLE
                }

            })

    }

    @SuppressLint("SetTextI18n")
    private fun showDots(img: Image) {
        val inflater = LayoutInflater.from(binding.root.context)
        /*val list = */
        /*list.add(Mark(coordinate_x = 40, coordinate_y = 20))
        list*/
        img.marks.forEach { mark->
            val price = PriceLayoutBinding.inflate(inflater)
            val imgView = ImageView(binding.root.context)
            imgView.setImageResource(R.drawable.ic_look_dot)
            binding.dotsSection.addView(imgView)
            binding.totalSection.addView(price.root)
            binding.root.post {
                imgView.x = binding.lookImg.width * (mark.coordinate_x / 100f)
                imgView.y = binding.lookImg.height * (mark.coordinate_y / 100f)
                val width = (price.root.width)
                if(imgView.x - width > 0)
                    price.root.x = (imgView.x - width + imgView.width / 2)
                else {
                    price.root.x = (imgView.x + imgView.width / 2)
                    price.imageView8.scaleX = -1f
                }
                price.root.y = imgView.y - (price.root.height / 2 * 0.76f)
            }

            try {
                Picasso.get()
                        .load(mark.brand_image)
                        .noPlaceholder()
                        .into(price.logo)
            }catch (e: Exception){

            }

            price.title.text = mark.label
            price.price.text = "${formatWithThousandsSeparator(mark.price)} ${mark.currency}"
        }
        binding.totalText.text = "${getString(R.string.total)} - ${formatWithThousandsSeparator(img.total)} ${getString(R.string.ruble)}"
    }
}