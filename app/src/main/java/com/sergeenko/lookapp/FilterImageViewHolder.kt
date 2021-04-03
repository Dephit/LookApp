package com.sergeenko.lookapp

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.sergeenko.lookapp.databinding.FilterImageViewBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class FilterImageViewHolder(itemView: View, val height: Int): RecyclerView.ViewHolder(
    itemView
) {

    private var xCoOrdinate: Float? = null
    private var yCoOrdinate: Float? = null
    val binding: FilterImageViewBinding = FilterImageViewBinding.bind(itemView)
    val attache: PhotoViewAttacher = PhotoViewAttacher(binding.img)

    init {
        binding.root.post {
            val lp = binding.root.layoutParams
            lp.width = height
            binding.root.layoutParams = lp
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun bind(file: FilterImage){
        if(file.bitmap == null) {
            Picasso.get()
                .load(Uri.fromFile(file.file))
                .placeholder(R.drawable.look_img_background)
                .into(binding.img, object : Callback {
                    override fun onSuccess() {
                        file.bitmap = binding.img.drawable.toBitmap()
                        /*binding.img.setScale(2f, false)
                        attache.update()*/
                    }

                    override fun onError(e: Exception?) {

                    }

                })
        }else {
            if(file.bitmapFiltered == null){
                binding.img.setImageBitmap(file.bitmap)
            }else {
                binding.img.setImageBitmap(file.bitmapFiltered)
            }

            binding.img.post {
                /*Log.i("SCALE", "${attache.minimumScale} ${attache.maximumScale} ${file.scale}, ${file.scaleX} ${file.scaleY}")
                binding.img.setScale(file.scale, file.scaleX, file.scaleY, false)
                attache.setScale(file.scale, file.scaleX, file.scaleY, false)
                attache.update()*/
            }
        }

        /*attache.setOnScaleChangeListener { scaleFactor, focusX, focusY ->
            file.scale = scaleFactor
            file.scaleX = focusX
            file.scaleY = focusY
        }*/

        /*binding.img.setOnTouchListener(OnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    xCoOrdinate = view.x - event.rawX
                    yCoOrdinate = view.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> view.animate().x(event.rawX + xCoOrdinate!!)
                    .y(event.rawY + yCoOrdinate!!).setDuration(0).start()
                else -> return@OnTouchListener false
            }
            true
        })*/

    }

}