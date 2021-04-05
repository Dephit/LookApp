package com.sergeenko.lookapp

import android.annotation.SuppressLint
import android.net.Uri
import android.view.MotionEvent
import android.view.ScaleGestureDetector
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
    fun bind(file: FilterImage, canScroll: ()-> Boolean){
        if(file.bitmap == null) {
            Picasso.get()
                .load(Uri.fromFile(file.file))
                .placeholder(R.drawable.look_img_background)
                .into(binding.img, object : Callback {
                    override fun onSuccess() {
                        file.bitmap = binding.img.drawable.toBitmap()
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
            binding.img.animate()
                    .x(file.xCoord)
                    .y(file.yCoord)
                    .scaleX(file.scale)
                    .scaleY(file.scale)
                    .setDuration(0)
                    .start()

            binding.img.post {
            }
        }

        var mScaleFactor = file.scale;
        val scaleGestureDetector = ScaleGestureDetector(itemView.context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                mScaleFactor *= detector?.scaleFactor ?: 1f;
                mScaleFactor = 0.1f.coerceAtLeast(mScaleFactor.coerceAtMost(10.0f));
                binding.img.animate().scaleX(mScaleFactor).scaleY(mScaleFactor).setDuration(0).start()
                file.scale = mScaleFactor
                return true;
            }
        })

        binding.img.setOnTouchListener(View.OnTouchListener { view, event ->
            if(!canScroll()) {
                if (event.pointerCount == 1) {
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            xCoOrdinate = view.x - event.rawX
                            yCoOrdinate = view.y - event.rawY
                        }
                        MotionEvent.ACTION_MOVE -> {
                            file.xCoord = event.rawX + xCoOrdinate!!
                            file.yCoord = event.rawY + yCoOrdinate!!
                            view.animate().x(event.rawX + xCoOrdinate!!)
                                .y(event.rawY + yCoOrdinate!!).setDuration(0).start()
                        }
                        else -> return@OnTouchListener false
                    }
                } else {
                    scaleGestureDetector.onTouchEvent(event);
                }
            }
            return@OnTouchListener true
        })
    }

}