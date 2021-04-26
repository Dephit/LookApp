    package com.sergeenko.lookapp.viewHolders

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.adapters.LookImgAdapter
import com.sergeenko.lookapp.databinding.*
import com.sergeenko.lookapp.interfaces.Repository
import com.sergeenko.lookapp.models.Look
import com.squareup.picasso.Callback
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
    private val more = getString(R.string.more)
    private var binding: LookViewBinding = LookViewBinding.bind(itemView)

    init {
        with(binding) {
            llm = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false);
            imgView.layoutManager = llm
            imgView.adapter = LookImgAdapter(listOf())
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(imgView)
        }
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    fun bind(look: Look, repository: Repository, viewModelScope: CoroutineScope, h: Int, disableScroll: (Boolean) -> Unit){
        this.repository = repository
        this.viewModelScope = viewModelScope
        with(binding){
            pageList.removeAllViews()
            hideWholePost(look, h, disableScroll)

            imgView.updateLayoutParams {
                this.height = h
            }

            header.updateLayoutParams {
                this.height = h
            }

            (imgView.adapter as LookImgAdapter).updateList(look.images) { setTouched(it) }

            setLookPost(look, h, disableScroll)
            setListeners(look, h, disableScroll)
            setAuthor(look)

            dislike.isActivated = look.is_dislike
            like.isActivated = look.is_like

            postDilsike.isActivated = look.is_dislike
            postLike.isActivated = look.is_like

            favorite.isActivated = look.is_favorite

            val itemsSize = (imgView.adapter as LookImgAdapter).list.size
            currentItemList.forEachIndexed { index, view ->
                if(index < itemsSize)
                    view.visibility = View.VISIBLE
                else
                    view.visibility = View.GONE
            }

            manageOwnPost(repository, look)
        }
    }

        private fun manageOwnPost(repository: Repository, look: Look) {
            /*if(look.user.id == repository.getUserFromDb()?.id){

            }*/
        }

        fun getString(total: Int): String {
        return itemView.context.getString(total)
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
            fill.visibility = View.GONE
            postSection.visibility = View.GONE
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

    private fun setLookPost(look: Look, height: Int, disableScroll: (Boolean) -> Unit) {
        if(look.type != "Лук"){
            setPost(look, height, disableScroll)
            binding.currentItemList.visibility = View.INVISIBLE
        }else{
            setLook(look)
            binding.currentItemList.visibility = View.VISIBLE
        }
        setAmount(binding.likesAmount, look.count_likes)
        setAmount(binding.commentsAmount, look.count_comments)
        setAmount(binding.dislikesAmount, look.count_dislikes)

        setAmount(binding.postLikesText, look.count_likes)
        setAmount(binding.postCommentsText, look.count_comments)
        setAmount(binding.postDislikesText, look.count_dislikes)
    }

    private fun setPost(look: Look, height: Int, disableScroll: (Boolean) -> Unit) {
        with(binding){
            imgView.visibility = View.GONE
            toPost.visibility = View.VISIBLE
            fill.visibility = View.VISIBLE

            /*preview.setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
            object : OnSwipeTouchListener(itemView.context) {

                override fun onSwipeTop() {
                    super.onSwipeTop()
                }

                override fun onSwipeBottom() {
                    super.onSwipeBottom()
                }

                override fun onSwipeLeft() {
                    showWholePost(look, height, disableScroll)
                    super.onSwipeLeft()
                }

                override fun onSwipeRight() {
                    super.onSwipeRight()
                }
            })*/

            Picasso.get()
                    .load(look.preview)
                    .noPlaceholder()
                    .error(R.drawable.background_splash)
                    .into(preview, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError(e: Exception?) {
                        }

                    })
        }
    }

    private fun setSpannableText(spannable: Spannable, look: Look, heightText: TextView, textID: String, toSpan: String) {
        val fc = object : ClickableSpan(){
            override fun onClick(widget: View) {
                toComments(widget, look)
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

    private fun setMoreSpannableText(look: Look, heightText: TextView, textID: String, toSpan: String) {
        val spannable = SpannableString(textID)

        setWhiteStannable(textID, toSpan)

        val fc = object : ClickableSpan(){
            override fun onClick(widget: View) {
                toComments(widget, look)
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

    private fun toComments(widget: View, look: Look) {
        Navigation.findNavController(widget).navigate(R.id.action_lookScrollingFragment_to_commentsFragment, bundleOf("look" to look))
    }

    private fun setWhiteStannable(textID: String, toSpan: String): SpannableString {
        val spannable = SpannableString(textID)
        val ss = StyleSpan(Typeface.BOLD)

        val indexStart = textID.indexOf(toSpan)
        val indexEnd = textID.indexOf(toSpan) + toSpan.length

        spannable.setSpan(ss, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }


    private fun setFatStannable(spannable: Spannable, toSpan: String) {
        val ss = StyleSpan(Typeface.BOLD)

        val indexStart = spannable.indexOf(toSpan)
        val indexEnd = spannable.indexOf(toSpan) + toSpan.length

        spannable.setSpan(ss, indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    @SuppressLint("SetTextI18n")
    private fun setAuthor(look: Look) {
        Picasso.get()
                .load(look.user.avatar)
                .noPlaceholder()
                .into(binding.autorImg)
        binding.autorImg.clipToOutline = true
        if(look.type != "Лук"){
            val lookText = "${look.user.username}\n${look.created}"
            binding.authorNick.text = lookText
            val spannable = setWhiteStannable(lookText, look.user.username)

            binding.authorNick.text = spannable

            binding.postText.text = look.title
        }else {
            var t = look.title
            try {
                if (t.length + look.user.username.length > 50) {
                    t = t.removeRange(t.length - " $more".length, t.length)
                }
            }catch (e: Exception){

            }

            val text = "${look.user.username} $t $more"

            val spannable = SpannableString(text)
            setSpannableText(spannable, look, binding.authorNick, text, look.user.username)
            setFatStannable(spannable, look.user.username)
            binding.authorNick.text = spannable

        }
    }

    private fun setListeners(look: Look, height: Int, disableScroll: (Boolean) -> Unit) {
        with(binding) {
            like.setOnClickListener {
                manageLike(it, look, height, disableScroll)
            }

            more.setOnClickListener { v->
                showAdditionalPostActions(
                        view = v,
                        x = more.x,
                        img = look,
                        onClaim = {
                            look.is_claim = true
                            viewModelScope?.launch {
                                repository?.claim(it, look.id)
                                        ?.catch {
                                            look.is_claim = false
                                            if (it.message == "HTTP 400 Bad Request") {
                                                Toast.makeText(v.context, getString(R.string.youve_sent_claim), Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(v.context, getString(R.string.cant_send_query), Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        ?.collect {

                                        }
                            }
                        },
                        onFavPressed = { view, look ->
                            manageFavorite(view, look)
                        }
                )
            }

            favorite.setOnClickListener {
                manageFavorite(it, look)
            }

            dislike.setOnClickListener {
                manageDislike(it, look, height, disableScroll)
            }

            comments.setOnClickListener {
                toComments(it, look)
            }

            postLike.setOnClickListener {
                like.callOnClick()
            }

            postDilsike.setOnClickListener {
                dislike.callOnClick()
            }

            postComments.setOnClickListener {
                comments.callOnClick()
            }

            toPost.setOnClickListener {
                showWholePost(look, height = height, disableScroll)
            }

            back.setOnClickListener {
                hideWholePost(look, height, disableScroll)
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

    private fun hideWholePost(look: Look, height: Int, disableScroll: (Boolean) -> Unit){
        binding.back.visibility = View.GONE
        binding.toPost.visibility = View.VISIBLE
        binding.pageList.visibility = View.GONE
        binding.postLikes.visibility = View.GONE

        binding.imgView.updateLayoutParams {
            this.height = height
        }

        binding.header.updateLayoutParams {
            this.height = height
        }

        look.isPostOpen = false
        binding.cv.radius = 0f
        disableScroll(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showWholePost(look: Look, height: Int, disableScroll: (Boolean) -> Unit) {
        if(binding.pageList.childCount == 0 && look.body.isNotEmpty()) {
            val inflater = LayoutInflater.from(itemView.context)
            look.body.forEach {
                when (it.type) {
                    "text" -> {
                        val tv = WholeTextViewBinding.inflate(inflater)
                        tv.text.text = it.content as String
                        binding.pageList.addView(tv.root)
                    }
                    "image" -> {
                        val tv = WholeImageBinding.inflate(inflater)
                        tv.image.maxHeight = (height * 0.7).toInt()
                        Picasso.get().load(it.content as String)
                                .noPlaceholder()
                                .into(tv.image, object : Callback {
                                    override fun onSuccess() {
                                        if (tv.image.height > height * 0.7) {
                                            val lp = tv.image.layoutParams
                                            lp.height = (height * 0.7).toInt()
                                            tv.image.layoutParams = lp
                                        }
                                        tv.image.clipToOutline = true
                                        tv.pb.visibility = View.GONE
                                    }

                                    override fun onError(e: java.lang.Exception?) {
                                        tv.image.visibility = View.GONE
                                    }

                                })
                        binding.pageList.addView(tv.root)
                    }
                    "post" -> {
                        val tv = LookViewHolder(LookViewBinding.inflate(inflater).root)
                        val tv1 = WholePostViewBinding.inflate(inflater)
                        val str = it.content.toString()
                                .replace("=http", "=\"http")
                                .replace(", profile=", "\", profile=")
                                .replace(", marks=", "\", marks=")
                                .replace("title=", "title=\"")
                                .replace(", images=", "\", images=")
                                .replace("created=", "created=\"")
                                .replace(", is_like=", "\", is_like=")

                        tv.bind(Gson().fromJson(str, Look::class.java), repository!!, viewModelScope!!, h = height, disableScroll)
                        tv.binding.more.visibility = View.GONE
                        tv1.card.addView(tv.binding.root)
                        binding.pageList.addView(tv1.root)
                    }
                }
            }
        }
        binding.postLikes.visibility = View.VISIBLE
        binding.back.visibility = View.VISIBLE
        binding.pageList.visibility = View.VISIBLE
        binding.toPost.visibility = View.GONE

        binding.cv.radius = itemView.context.resources.getDimension(R.dimen._25sdp)

        binding.imgView.updateLayoutParams {
            this.height = (height * 0.7f).toInt()
        }

        binding.header.updateLayoutParams {
            this.height = (height * 0.7f).toInt()
        }

        look.isPostOpen = true

        var canDo = false

        /*binding.root.setOnTouchListener(@SuppressLint("ClickableViewAccessibility")
        object : OnSwipeTouchListener(itemView.context, 100, 100) {

            var canDo = false

            override fun onSwipeTop() {
                val diff: Int = binding.sv.getChildAt(binding.sv.childCount - 1).bottom - (binding.sv.height + binding.sv.scrollY)

                if (diff == 0) {
                    Handler(Looper.myLooper()!!).postDelayed(
                            {
                                canDo = true
                            }, 250
                    )
                }
                if (diff == 0 && canDo) {
                    hideWholePost(look, height, disableScroll)
                }
                super.onSwipeTop()
            }

            override fun onSwipeBottom() {
                canDo = false
                Handler(Looper.myLooper()!!).postDelayed(
                        {
                            canDo = false
                        }, 250
                )
                super.onSwipeBottom()
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
            }
        })*/

        disableScroll(false)
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
                    ?.collect {
                        look.favorite_id = it
                    }
        }
    }

    private fun manageLike(view: View, look: Look, height: Int, disableScroll: (Boolean) -> Unit) {
        fun setLike(height: Int, disableScroll: (Boolean) -> Unit) {
            look.is_like = !look.is_like
            binding.like.isActivated = look.is_like
            binding.postLike.isActivated = look.is_like
            if(look.is_like) {
                look.count_likes++
                showFavorite(look)
                showLike(R.drawable.ic_like_activated_pressed)
                if(look.is_dislike){
                    look.is_dislike = false
                    binding.dislike.isActivated = false
                    binding.postDilsike.isActivated = false
                    look.count_dislikes--
                }
            }else{
                look.count_likes--
                hideFavorite()
            }
            setLookPost(look, height, disableScroll)
        }

        viewModelScope?.launch {
            setLike(height, disableScroll)
            repository?.like(like = look.is_like, postID = look.id)
                    ?.onStart { view.isEnabled = false }
                    ?.catch {
                        setLike(height, disableScroll)
                        Toast.makeText(view.context, getString(R.string.cant_send_query), Toast.LENGTH_SHORT).show()
                    }
                    ?.onCompletion { view.isEnabled = true }
                    ?.collect {  }
        }
    }

    private fun manageDislike(view: View, look: Look, height: Int, disableScroll: (Boolean) -> Unit) {
        fun setDislike(){
            look.is_dislike = !look.is_dislike
            binding.dislike.isActivated = look.is_dislike
            binding.postDilsike.isActivated = look.is_dislike
            if(look.is_dislike) {
                look.count_dislikes++
                showLike(R.drawable.ic_dislike_activated_pressed)
                if (look.is_like) {
                    look.is_like = false
                    binding.like.isActivated = false
                    binding.postLike.isActivated = false
                    look.count_likes--
                }
            }else{
                look.count_dislikes--
                hideFavorite()
            }
            setLookPost(look, height, disableScroll)//setLook(look)
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

inline fun showAdditionalPostActions(view: View, x: Float, img: Look, crossinline onClaim: (String) -> Unit, crossinline onFavPressed: (View, Look) -> Unit) {

    val customLayout = AdditionActionsLayoutBinding.inflate(LayoutInflater.from(view.context))

    // create an alert builder
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val focusable = true // lets taps outside the popup also dismiss it

    val window = PopupWindow(customLayout.root, width, height, focusable)
    window.isOutsideTouchable = true

    customLayout.addToFavText.text = if(img.is_favorite)
        view.context.getString(R.string.delete_from_favorite)
    else
        view.context.getString(R.string.add_to_fav_text)

    customLayout.complain.setOnClickListener {
        if(img.is_claim){
            customLayout.actions.visibility = View.GONE
            customLayout.complainDetailed.visibility = View.GONE
            customLayout.complainSend.visibility = View.VISIBLE
        }else{
            customLayout.actions.visibility = View.GONE
            customLayout.complainDetailed.visibility = View.VISIBLE
            customLayout.complainSend.visibility = View.GONE
        }
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
        onClaim("Спам")
        customLayout.actions.visibility = View.GONE
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
        window.dismiss()
    }

    customLayout.shockContent.setOnClickListener {
        onClaim("Шокирующий контент")
        customLayout.actions.visibility = View.GONE
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
        window.dismiss()
    }

    customLayout.incorrectInformation.setOnClickListener {
        onClaim("Некорректная информация")
        customLayout.actions.visibility = View.GONE
        customLayout.complainDetailed.visibility = View.GONE
        customLayout.complainSend.visibility = View.VISIBLE
        window.dismiss()
    }

    customLayout.sharePost.setOnClickListener {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://lookatback.com/api/posts/${img.id}")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        it.context.startActivity(shareIntent)
        window.dismiss()
    }

    customLayout.copyLink.setOnClickListener {
        val clipboard: ClipboardManager? = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("label", "https://lookatback.com/api/posts/${img.id}")
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