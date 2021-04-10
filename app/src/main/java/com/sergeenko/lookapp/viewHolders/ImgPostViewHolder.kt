package com.sergeenko.lookapp.viewHolders

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import com.sergeenko.lookapp.models.Img
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.databinding.PostLookViewBinding
import com.sergeenko.lookapp.databinding.PriceLayoutBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ImgPostViewHolder(itemView: View) : LookViewViewHolder(itemView) {

    val binding = PostLookViewBinding.bind(itemView)

    fun bind(img: Img) {
        loadImg(img)
        if(img.isPost){
            setPost(img)
        }else {
            setLook(img)
        }
        setSpannable(img)
        setListeners(img)
    }

    private fun setPost(img: Img) {
        with(binding){
            dotsSection.visibility = View.GONE
            totalSection.visibility = View.GONE
            postSection.visibility = View.VISIBLE

            postText.text = img.postText
        }
    }

    private fun setLook(img: Img) {
        with(binding) {
            dotsSection.visibility = View.VISIBLE
            totalSection.visibility = View.VISIBLE
            postSection.visibility = View.GONE

            setAmount(likesAmount, img.likes)
            setAmount(commentsAmount, img.comments)
            setAmount(dislikesAmount, img.dislikes)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setSpannable(img: Img) {
        if(img.isPost){
            binding.authorNick.text = "${img.author}\n${img.postDate}"
            binding.authorNick.text = setWhiteStannable("${img.author}\n${img.postDate}", img.author)
        }else {
            var t = img.text
            if (t.length + img.author.length > 50){
                t = t.removeRange(t.length - " $more".length, t.length)
            }
            val text = "${img.author} $t $more"
            setSpannableText(binding.authorNick, text, img.author)
        }
    }

    private fun setListeners(img: Img) {
        with(binding){
            like.setOnClickListener {
                it.isActivated = !it.isActivated
                if(it.isActivated) {
                    showFavorite(img)
                    showLike(R.drawable.ic_like_pressed)
                    dislike.isActivated = false
                }else{
                    hideFavorite()
                }
            }

            favorite.setOnClickListener {
                img.isFavorite = !img.isFavorite
                checkFavorite(img)
            }

            dislike.setOnClickListener {
                it.isActivated = !it.isActivated
                if(it.isActivated) {
                    showLike(R.drawable.ic_dislike_pressed)
                    hideFavorite()
                    like.isActivated = false
                }
            }

            comments.setOnClickListener {
                //Navigation.findNavController(it).navigate(R.id.action_lookScrollingFragment_to_commentsFragment)
            }

            root.setOnClickListener {
                if(!img.isPost) {
                    showTotal()
                }
                hideFavorite()
            }

            this.toPost.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_lookScrollingFragment_to_postFragment)
            }

            autorImg.setOnClickListener {
                /*Navigation.findNavController(it).navigate(R.id.action_lookScrollingFragment_to_profileFragment)*/
            }
        }
    }

    private fun checkFavorite(img: Img) {
        binding.favorite.isActivated = img.isFavorite
    }

    private fun showTotal() {
        if(binding.favorite.visibility == View.GONE){
            if(binding.totalSection.alpha < 0.5f){
                binding.totalSection.clearAnimation()
                binding.commentSection.clearAnimation()
                binding.totalSection.animate().alpha(1.0f).setDuration(1000L)
                binding.commentSection.animate().alpha(0f).setDuration(1000L)
            }else {
                binding.totalSection.clearAnimation()
                binding.commentSection.clearAnimation()
                binding.totalSection.animate().alpha(0f).setDuration(1000L)
                binding.commentSection.animate().alpha(1.0f).setDuration(1000L)
            }
        }
    }

    private fun setSpannableText(heightText: TextView, textID: String, toSpan: String) {
        val spannable = SpannableString(textID)

        setWhiteStannable(textID, toSpan)

        val fc = object : ClickableSpan(){
            override fun onClick(widget: View) {
/*                Navigation.findNavController(widget).navigate(R.id.action_lookScrollingFragment_to_commentsFragment)*/
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.linkColor = Color.WHITE
            }
        }

        val indexMoreStart = textID.indexOf(more)
        val indexMoreEnd = textID.indexOf(more) + more.length

        spannable.setSpan(
            UnderlineSpan(), indexMoreStart, indexMoreEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(fc, indexMoreStart, indexMoreEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        heightText.movementMethod = LinkMovementMethod.getInstance()
        heightText.text = spannable
    }

    private fun setWhiteStannable(textID: String, toSpan: String): SpannableString {
        val spannable = SpannableString(textID)
        val ss = StyleSpan(Typeface.BOLD)

        val indexStart = textID.indexOf(toSpan)
        val indexEnd = textID.indexOf(toSpan) + toSpan.length

        spannable.setSpan(ss, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    private fun showLike(drawId: Int) {
        binding.likeBig.setImageResource(drawId)
        binding.likeBig.animate().scaleX(5f).scaleY(5f).setDuration(1000).setListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                binding.likeBig.visibility = View.VISIBLE

            }

            override fun onAnimationEnd(animation: Animator?) {
                binding.likeBig.visibility = View.GONE
                binding.likeBig.scaleX = 0f
                binding.likeBig.scaleY = 0f
            }

            override fun onAnimationCancel(animation: Animator?) {
                binding.likeBig.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

        })
    }

    private fun hideFavorite() {
        binding.favorite.visibility = View.GONE
    }

    private fun setAmount(tv: TextView, amt: Long) {
        val amount = when {
            amt > 9999 -> "${amt / 1000}${tv.context.getString(R.string.thousand)}"
            amt > 999999 -> "${amt / 1000000}${tv.context.getString(R.string.millions)}"
            else -> "$amt"
        }
        tv.text = amount
    }

    private fun showFavorite(look: Img) {
        checkFavorite(look)
        binding.favorite.visibility = View.VISIBLE
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(
                {
                    hideFavorite()
                }, 3000L
        )
    }

    private fun loadImg(img: Img) {
        binding.autorImg.clipToOutline = true
        Picasso.get()
                .load("https://picsum.photos/${img.w}/${img.h}")
                .noPlaceholder()
                .error(R.drawable.background_splash)
                .into(binding.lookImg, object : Callback {
                    override fun onSuccess() {
                        binding.lookImg.post {
                            binding.lookImg.clipToOutline = true
                        }
                        binding.progressBar.visibility = View.GONE
                        if (!img.isPost) {
                            showDots(img)
                        }
                    }

                    override fun onError(e: Exception?) {
                        binding.progressBar.visibility = View.GONE
                        binding.errorText.visibility = View.VISIBLE
                    }

                })

    }

    @SuppressLint("SetTextI18n")
    private fun showDots(img: Img) {
        var total = 0
        val inflater = LayoutInflater.from(binding.root.context)
        img.dots.forEach {
            val price = PriceLayoutBinding.inflate(inflater)
            val imgView = ImageView(binding.root.context)
            imgView.setImageResource(R.drawable.ic_look_dot)
            binding.dotsSection.addView(imgView)
            binding.totalSection.addView(price.root)
            binding.root.post {
                imgView.x = binding.lookImg.width * (it.first / 100f)
                imgView.y = binding.lookImg.height * (it.second / 100f)
                val width = (price.root.width * 0.89f)
                if(imgView.x - width > 0)
                    price.root.x = (imgView.x - width)
                else {
                    price.root.x = (imgView.x + 2)
                    price.imageView8.scaleX = -1f
                }
                price.root.y = imgView.y - (price.root.height / 2 * 0.76f)
            }

            price.price.text = "${it.first} ${it.second}"
            total += it.first + it.second
        }
        binding.totalText.text = "${getString(R.string.total)} - $total ${getString(R.string.ruble)}"
    }
}