package com.sergeenko.lookapp.viewHolders

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.adapters.FilterImage
import com.sergeenko.lookapp.databinding.FilterImageViewBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class FilterImageViewHolder(itemView: View, val width: Int, private val scope: CoroutineScope): RecyclerView.ViewHolder(itemView) {

    private var xCoOrdinate: Float? = null
    private var yCoOrdinate: Float? = null
    val binding: FilterImageViewBinding = FilterImageViewBinding.bind(itemView)

    init {
        binding.root.post {
            val lp = binding.root.layoutParams
            lp.width = width
            binding.root.layoutParams = lp
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun bind(file: FilterImage, canScroll: ()-> Boolean){
        if(file.bitmap == null) {
            loadImgByPicasso(file)
        }else {
            setImage(file)
            setBackgroundColor(file)
            setScale(file)
        }

        flowFile(file)
        setListeners(file, canScroll)
    }

    @SuppressLint("ClickableViewAccessibility")
    private inline fun setListeners(file: FilterImage, crossinline canScroll: () -> Boolean) {
        var mScaleFactor = file.scale
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

    private fun loadImgByPicasso(file: FilterImage) {
        binding.progressBar.visibility = View.VISIBLE
        Picasso.get()
                .load(file.file)
                .placeholder(R.drawable.look_img_background)
                .into(binding.img, object : Callback {
                    override fun onSuccess() {
                        file.bitmap = binding.img.drawable.toBitmap()
                        binding.progressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {

                    }

                })
    }

    private fun flowFile(file: FilterImage) {
        scope.launch {
            file.rotateFlow
                    .catch {  }
                    .collect {
                        try{
                            with(binding.img){
                                rotationY = file.rotationZ
                                rotation = file.rotationX
                                rotationX = file.rotationY
                                colorFilter = file.setBrightness(file.contrast, file.brightness.toFloat() * 2)
                            }
                        }catch (e: Exception){

                        }
                    }
        }
    }

    private fun setScale(file: FilterImage) {
        binding.img.animate()
                .x(file.xCoord)
                .y(file.yCoord)
                .scaleX(file.scale)
                .scaleY(file.scale)
                .setDuration(0)
                .start()
    }

    private fun setImage(file: FilterImage) {
        if(file.bitmapFiltered == null){
            binding.img.setImageBitmap(file.bitmap)
        }else {
            binding.img.setImageBitmap(file.bitmapFiltered)
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun setBackgroundColor(file: FilterImage) {
        file.backgroundColor?.let { it1 ->
            binding.img.setBackgroundColor(Color.parseColor(it1))
            binding.background.setBackgroundColor(Color.parseColor(it1))
        } ?: run{
            binding.img.setBackgroundColor(Color.parseColor(file.oldBackgroundColor))
            binding.background.setBackgroundColor(Color.parseColor(file.oldBackgroundColor))
        }
    }

}