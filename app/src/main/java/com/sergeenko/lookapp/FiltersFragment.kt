package com.sergeenko.lookapp

import android.R.drawable
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.view.forEachIndexed
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.FilterViewBinding
import com.sergeenko.lookapp.databinding.FiltersFragmentBinding
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.utils.ThumbnailCallback
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class FiltersFragment : BaseFragment<FiltersFragmentBinding>() {

    override val viewModel: FiltersViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): FiltersFragmentBinding = FiltersFragmentBinding.inflate(
        inflater
    )

    lateinit var llm: LinearLayoutManager

    private fun setRV(fileList: List<FilterImage>) {
        val adapter = viewModel.adapter
        adapter.setList(_fileList = fileList)
        llm = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rv.layoutManager = llm
        binding.rv.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rv)
        binding.rv.scrollToPosition(0)
    }

    override fun setListeners() {
        withBinding {
            setFilters()
            val file = arguments?.getSerializable("files") as List<File>

            currentItemList.forEachIndexed { index, view ->
                if(index < file.size)
                    view.visibility = View.VISIBLE
                else
                    view.visibility = View.GONE
            }
            setRV(file.map { FilterImage(it, null) })
            /*binding.previewImage.visibility = View.GONE
            binding.progressBar4.visibility = View.VISIBLE*/
            /*Picasso.get()
                    .load(file[0])
                    .noPlaceholder()
                    .into(binding.previewImage, object : Callback {
                        override fun onSuccess() {
                            binding.previewImage.visibility = View.VISIBLE
                            binding.progressBar4.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {

                        }

                    })*/
        }
    }

    private fun setFilters() {
        val filters: List<Filter> = FilterPack.getFilterPack(context)
        val inflater = LayoutInflater.from(context)

        val handler = Handler(Looper.myLooper()!!)
        val r = Runnable {
            val thumbImage = BitmapFactory.decodeResource(resources, R.drawable.look_mock_bg)
            ThumbnailsManager.clearThumbs()

            filters.forEach {filter->
                    val thumbnailItem = ThumbnailItem();
                    thumbnailItem.image = thumbImage;
                    thumbnailItem.filter = filter;
                    thumbnailItem.filterName = filter.name
                    ThumbnailsManager.addThumb(thumbnailItem);
                }

            val thumbs = ThumbnailsManager.processThumbs(context)
            thumbs.forEach {
                val img = FilterViewBinding.inflate(inflater, null, false)
                img.filterImg.setImageBitmap(it.image)
                img.filterImg.clipToOutline = true
                img.textView.text = it.filterName
                img.filterImg.setOnClickListener {_->
                    viewModel.applyFilter((llm.findFirstVisibleItemPosition() + llm.findLastVisibleItemPosition()) / 2, it.filter)
                }
                binding.filtersList.addView(img.root)
            }

        };
        handler.post(r)
    }

}