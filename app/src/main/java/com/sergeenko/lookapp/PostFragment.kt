package com.sergeenko.lookapp

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sergeenko.lookapp.databinding.PostDetailedLikeSectionBinding
import com.sergeenko.lookapp.databinding.PostFragmentBinding
import com.sergeenko.lookapp.databinding.PostLookViewBinding
import com.sergeenko.lookapp.databinding.PostTextViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostFragment : BaseFragment<PostFragmentBinding>() {

    override val viewModel: PostViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): PostFragmentBinding {
        return PostFragmentBinding.inflate(inflater)
    }

    override fun setListeners() {
        withBinding {
            val inflater = LayoutInflater.from(context)
            val firstLook = arguments?.getSerializable("post") as Img
            val firstImg = PostLookViewBinding.inflate(inflater)


            firstImg.toPost.visibility = View.GONE

            val firstHolder = ImgPostViewHolder(firstImg.root)
            firstHolder.bind(firstLook)
            firstHolder.itemView.post {
                val lp = firstHolder.itemView.layoutParams
                lp.height = resources.getDimension(R.dimen._483sdp).toInt()
                firstHolder.itemView.layoutParams = lp
            }

            postList.addView(firstHolder.itemView)

            for(i in 0..5){
                val img = PostLookViewBinding.inflate(inflater)
                val text = PostTextViewBinding.inflate(inflater)

                text.text.text = "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit. Exercitation veniam consequat sunt nostrud amet."
                val holder = ImgPostViewHolder(img.root)
                holder.bind(viewModel.getImg())
                holder.itemView.post {
                    val lp = holder.itemView.layoutParams
                    lp.height = resources.getDimension(R.dimen._483sdp).toInt()
                    holder.itemView.layoutParams = lp
                }
                postList.addView(holder.itemView)
                postList.addView(text.root)
            }
            setPostLikesSection(inflater, firstLook)


            back.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setPostLikesSection(inflater: LayoutInflater, img: Img) {
        val postLike = PostDetailedLikeSectionBinding.inflate(inflater)
        postLike.postLikesText.text = img.likes.toString()
        postLike.postDislikesText.text = img.dislikes.toString()
        postLike.postCommentsText.text = img.comments.toString()
        postLike.postComments.setOnClickListener {

        }

        postLike.postLike.setOnClickListener {
            it.isActivated = !it.isActivated
        }

        postLike.postDilsike.setOnClickListener {
            it.isActivated = !it.isActivated
        }
        binding.postList.addView(postLike.root)
    }


}