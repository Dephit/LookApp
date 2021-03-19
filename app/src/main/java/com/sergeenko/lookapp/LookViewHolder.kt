package com.sergeenko.lookapp

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.LookViewBinding
import com.sergeenko.lookapp.models.Look
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class LookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var repository: Repository? = null
    private var viewModelScope: CoroutineScope? = null

    var handler: Handler = Handler(Looper.myLooper()!!)
    private var llm: LinearLayoutManager
    val more = getString(R.string.more)
    private var binding: LookViewBinding = LookViewBinding.bind(itemView)

    fun getString(total: Int): String {
        return itemView.context.getString(total)
    }

    init {
        with(binding) {
            llm = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false);
            imgView.layoutManager = llm
            imgView.adapter = LookImgAdapter(listOf())
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(imgView)
        }
    }

    private fun setAmount(tv: TextView, amt: Int) {
        val amount = when {
            amt > 9999 -> "${amt / 1000}${tv.context.getString(R.string.thousand)}"
            amt > 999999 -> "${amt / 1000000}${tv.context.getString(R.string.millions)}"
            else -> "$amt"
        }
        tv.text = amount
    }

    private fun setLook(img: Look) {
        with(binding) {
            /*dotsSection.visibility = View.VISIBLE
            totalSection.visibility = View.VISIBLE*/
            postSection.visibility = View.GONE

            setAmount(likesAmount, img.count_likes)
            setAmount(commentsAmount, img.count_comments)
            setAmount(dislikesAmount, img.count_dislikes)
        }
    }

    private fun setTouched(view: View){
        if(binding.favorite.visibility == View.GONE){
            if(binding.commentSection.alpha > 0.5f){
                view.clearAnimation()
                binding.commentSection.clearAnimation()
                view.animate().alpha(1.0f).duration = 1000L
                binding.commentSection.animate().alpha(0f).duration = 1000L
            }else {
                view.clearAnimation()
                binding.commentSection.clearAnimation()
                view.animate().alpha(0f).duration = 1000L
                binding.commentSection.animate().alpha(1.0f).duration = 1000L
            }
        }
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

    private fun showFavorite(look: Look) {
        binding.favorite.visibility = View.VISIBLE
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(
            {
                hideFavorite()
            }, 3000L
        )
    }

    private fun checkFavorite(img: Look) {
        binding.favorite.isActivated = !binding.favorite.isActivated//img.isFavorite
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    fun bind(look: Look, repository: Repository, viewModelScope: CoroutineScope){
        this.repository = repository
        this.viewModelScope = viewModelScope
        with(binding){
            (imgView.adapter as LookImgAdapter).updateList(look.images) { setTouched(it) }

            if(look.type != "Лук"){
                currentItemList.visibility = View.INVISIBLE
            }else{
                setLook(look)
                currentItemList.visibility = View.VISIBLE
            }

            setListeners(look)
            setAuthor(look)
            dislike.isActivated = look.is_dislike
            like.isActivated = look.is_like
            favorite.isActivated = look.is_favorite

            val itemsSize = (imgView.adapter as LookImgAdapter).list.size
            currentItemList.forEachIndexed { index, view ->
                if(index < itemsSize)
                    view.visibility = View.VISIBLE
                else
                    view.visibility = View.GONE
            }
        }
    }

    private fun setSpannableText(look: Look, heightText: TextView, textID: String, toSpan: String) {
        val spannable = SpannableString(textID)

        setWhiteStannable(textID, toSpan)

        val fc = object : ClickableSpan(){
            override fun onClick(widget: View) {
                Navigation.findNavController(widget).navigate(R.id.action_lookScrollingFragment_to_commentsFragment, bundleOf("look" to look))
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.linkColor = Color.WHITE
            }
        }

        val indexMoreStart = textID.indexOf(more)
        val indexMoreEnd = textID.indexOf(more) + more.length

        spannable.setSpan(UnderlineSpan(), indexMoreStart, indexMoreEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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

    @SuppressLint("SetTextI18n")
    private fun setAuthor(look: Look) {
        Picasso.get()
                .load(look.user.avatar)
                .noPlaceholder()
                .into(binding.autorImg)
        binding.autorImg.clipToOutline = true
        if(look.type != "Лук"){
            val lookText = "${look.user.username}\n${look.title}"
            binding.authorNick.text = lookText
            binding.authorNick.text = setWhiteStannable(lookText, look.user.username)
        }else {
            var t = look.title
            if (t.length + look.user.username.length > 50){
                t = t.removeRange(t.length - " $more".length, t.length)
            }
            val text = "${look.user.username} $t $more"
            setSpannableText(look, binding.authorNick, text, look.user.username)
        }
    }

    private fun setListeners(look: Look) {
        with(binding) {
            like.setOnClickListener {
                manageLike(it, look)
            }

            more.setOnClickListener {
                showAdditionalActions(it, more.x, look){view, look->
                    manageFavorite(view, look)
                }
            }

            favorite.setOnClickListener {
                manageFavorite(it, look)
            }

            dislike.setOnClickListener {
                    manageDislike(it, look)
                }

            comments.setOnClickListener {
                  Navigation.findNavController(it).navigate(R.id.action_lookScrollingFragment_to_commentsFragment, bundleOf("look" to look))
              }

            toPost.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.action_lookScrollingFragment_to_postFragment)
            }

            autorImg.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.action_lookScrollingFragment_to_profileFragment)
            }

            imgView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val first: Int = llm.findFirstVisibleItemPosition()
                    val last: Int = llm.findLastVisibleItemPosition()
                    val newPosition = (first + last) / 2
                    currentItemList.forEachIndexed { _, view ->
                        view.isSelected = false
                    }
                    currentItemList[newPosition].isSelected = true
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
    }

    private fun manageFavorite(view: View, look: Look) {
        fun setFavorite(){
            look.is_favorite = !look.is_favorite
            view.isActivated = look.is_favorite
        }

        viewModelScope?.launch {
            setFavorite()
            repository?.favorite(like = look.is_favorite, post = look)
                    ?.onStart { view.isEnabled = false }
                    ?.catch {
                        setFavorite()
                        Toast.makeText(view.context, getString(R.string.cant_send_query), Toast.LENGTH_SHORT).show()
                    }
                    ?.onCompletion { view.isEnabled = true }
                    ?.collect {  }
        }
    }

    private fun manageLike(view: View, look: Look) {
        fun setLike(){
            look.is_like = !look.is_like
            view.isActivated = look.is_like
            if(look.is_like) {
                look.count_likes++
                showFavorite(look)
                showLike(R.drawable.ic_like_activated_pressed)
                if(look.is_dislike){
                    look.is_dislike = false
                    binding.dislike.isActivated = false
                    look.count_dislikes--
                }
            }else{
                look.count_likes--
                hideFavorite()
            }
            setLook(look)
        }

        viewModelScope?.launch {
            setLike()
            repository?.like(like = look.is_like, postID = look.id)
                    ?.onStart { view.isEnabled = false }
                    ?.catch {
                        setLike()
                        Toast.makeText(view.context, getString(R.string.cant_send_query), Toast.LENGTH_SHORT).show()
                    }
                    ?.onCompletion { view.isEnabled = true }
                    ?.collect {  }
        }
    }

    private fun manageDislike(view: View, look: Look) {
        fun setDislike(){
            look.is_dislike = !look.is_dislike
            view.isActivated = look.is_dislike
            if(look.is_dislike) {
                look.count_dislikes++
                showLike(R.drawable.ic_dislike_activated_pressed)
                if (look.is_like) {
                    look.is_like = false
                    binding.like.isActivated = false
                    look.count_likes--
                }
            }else{
                look.count_dislikes--
                hideFavorite()
            }
            setLook(look)
        }

        viewModelScope?.launch {
            setDislike()
            repository?.dislike(dislike = look.is_dislike, postID = look.id)
                    ?.onStart { view.isEnabled = false }
                    ?.catch {
                        setDislike()
                        Toast.makeText(view.context, getString(R.string.cant_send_query), Toast.LENGTH_SHORT).show()
                    }
                    ?.onCompletion { view.isEnabled = true }
                    ?.collect {  }
        }
    }
}

