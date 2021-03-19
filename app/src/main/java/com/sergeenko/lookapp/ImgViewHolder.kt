package com.sergeenko.lookapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.sergeenko.lookapp.databinding.AdditionActionsLayoutBinding
import com.sergeenko.lookapp.databinding.LookImgBinding
import com.sergeenko.lookapp.databinding.PriceLayoutBinding
import com.sergeenko.lookapp.models.Image
import com.sergeenko.lookapp.models.Look
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

    private fun loadImg(img: Image) {
        showDots(img)
        Picasso.get()
            .load(img.url)
            .noPlaceholder()
            .error(R.drawable.background_splash)
            .into(binding.lookImg, object : Callback {
                override fun onSuccess() {
                    binding.lookImg.post {
                        binding.lookImg.clipToOutline = true
                    }
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
        img.marks.forEach {mark->
            val price = PriceLayoutBinding.inflate(inflater)
            val imgView = ImageView(binding.root.context)
            imgView.setImageResource(R.drawable.ic_look_dot)
            binding.dotsSection.addView(imgView)
            binding.totalSection.addView(price.root)
            binding.root.post {
                imgView.x = binding.lookImg.width * (mark.coordinate_x / 100f)
                imgView.y = binding.lookImg.height * (mark.coordinate_y / 100f)
                val width = (price.root.width * 0.89f)
                if(imgView.x - width > 0)
                    price.root.x = (imgView.x - width)
                else {
                    price.root.x = (imgView.x + 2)
                    price.imageView8.scaleX = -1f
                }
                price.root.y = imgView.y - (price.root.height / 2 * 0.76f)
            }

            Picasso.get()
                .load(mark.brand_image)
                .noPlaceholder()
                .into(price.logo)

            price.title.text = mark.label
            price.price.text = "${mark.price} ${mark.currency}"
        }
        binding.totalText.text = "${getString(R.string.total)} - ${img.total} ${getString(R.string.ruble)}"
    }
}

inline fun showAdditionalActions(view: View, x: Float, img: Look, crossinline onFavPressed: (View, Look)-> Unit) {

    val customLayout = AdditionActionsLayoutBinding.inflate(LayoutInflater.from(view.context))

    // create an alert builder
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val focusable = true // lets taps outside the popup also dismiss it

    val window = PopupWindow(customLayout.root, width, height, focusable)
    window.isOutsideTouchable = true


    customLayout.complain.setOnClickListener {
        customLayout.actions.visibility = View.GONE
        customLayout.complainDetailed.visibility = View.VISIBLE
        customLayout.complainSend.visibility = View.GONE
    }

    customLayout.back.setOnClickListener {
        customLayout.actions.visibility = View.VISIBLE
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.GONE
    }

    customLayout.backToActions.setOnClickListener {
        customLayout.actions.visibility = View.VISIBLE
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.GONE
    }

    customLayout.spam.setOnClickListener {
        customLayout.actions.visibility = View.GONE
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
    }

    customLayout.shockContent.setOnClickListener {
        customLayout.actions.visibility = View.GONE
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
    }

    customLayout.incorrectInformation.setOnClickListener {
        customLayout.actions.visibility = View.GONE
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
    }

    customLayout.sharePost.setOnClickListener {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        it.context.startActivity(shareIntent)
        window.dismiss()
    }

    customLayout.copyLink.setOnClickListener {
        val clipboard: ClipboardManager? = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("label", "link")
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(it.context, it.context.getString(R.string.link_is_copied), Toast.LENGTH_SHORT).show()
        window.dismiss()
    }

    customLayout.addToFav.isActivated = img.is_favorite
    customLayout.addToFav.setOnClickListener {
        onFavPressed(it, img)
        window.dismiss()
    }

    window.contentView = customLayout.root
    window.showAtLocation(view, Gravity.TOP, x.toInt(), 0)
}