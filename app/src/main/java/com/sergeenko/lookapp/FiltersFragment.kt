package com.sergeenko.lookapp

import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.FilterViewBinding
import com.sergeenko.lookapp.databinding.FiltersFragmentBinding
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class FiltersFragment : BaseFragment<FiltersFragmentBinding>() {

    private var thumbs: MutableList<ThumbnailItem> = mutableListOf()
    override val viewModel: FiltersViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): FiltersFragmentBinding = FiltersFragmentBinding.inflate(
            inflater
    )

    lateinit var llm: LinearLayoutManager

    private fun setRV(fileList: List<FilterImage>) {
        val adapter = viewModel.adapter
        llm = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rv.layoutManager = llm
        binding.rv.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rv)
        adapter.setList(_fileList = fileList)
        /*Handler(Looper.myLooper()!!).postDelayed(
                {
                    llm.scrollToPosition(1)
                }, 1000
        )*/

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val newPosition = currentPosition()
                binding.currentItemList.forEachIndexed { _, view ->
                    view.isSelected = false
                }
                binding.currentItemList[newPosition].isSelected = true
                binding.filtersList.forEachIndexed { index, view ->
                    val b = FilterViewBinding.bind(view)
                    b.textView.isActivated = thumbs[index].filter == viewModel.adapter.getCurrentFile(newPosition).filter
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun setListeners() {
        withBinding {
            setFilters()
            val file = arguments?.getSerializable("files") as List<File>

            toolbarGallary.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            currentItemList.forEachIndexed { index, view ->
                if(index < file.size)
                    view.visibility = View.VISIBLE
                else
                    view.visibility = View.GONE
            }

            setRV(file.map { FilterImage(it, null) })
        }
    }

    private fun setFilters() {
        val filters: List<Filter> = FilterPack.getFilterPack(context)
        val inflater = LayoutInflater.from(context)

        val handler = Handler(Looper.myLooper()!!)
        val r = Runnable {
            val thumbImage = BitmapFactory.decodeResource(resources, R.drawable.look_mock_bg)
            ThumbnailsManager.clearThumbs()

            filters.forEach { filter->
                    val thumbnailItem = ThumbnailItem();
                    thumbnailItem.image = thumbImage;
                    thumbnailItem.filter = filter;
                    thumbnailItem.filterName = filter.name
                    ThumbnailsManager.addThumb(thumbnailItem);
                }

            thumbs = ThumbnailsManager.processThumbs(context)
            thumbs.forEach {
                val img = FilterViewBinding.inflate(inflater, null, false)
                img.filterImg.setImageBitmap(it.image)
                img.filterImg.clipToOutline = true
                img.textView.text = it.filterName
                img.filterImg.setOnClickListener { _->
                    binding.filtersList.forEach { view->
                        FilterViewBinding.bind(view).textView.isActivated = false
                    }
                    img.textView.isActivated = viewModel.applyFilter(currentPosition(), it.filter)
                }
                binding.filtersList.addView(img.root)
            }

        };
        handler.post(r)
    }

    private fun currentPosition(): Int = (llm.findFirstVisibleItemPosition() + llm.findLastVisibleItemPosition()) / 2

}

class CenterRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun updatePadding() {
        post {
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels
            val firstViewHolder = findViewHolderForAdapterPosition(0)
            if (firstViewHolder != null) {
                firstViewHolder.itemView.measure(WRAP_CONTENT, WRAP_CONTENT)
                val viewWidth = firstViewHolder.itemView.measuredWidth
                val padding: Int
                padding = if (screenHeight > screenWidth) {
                    //Portrait
                    screenWidth / 2 - viewWidth / 2
                } else {
                    //Landscape
                    screenHeight / 2 - viewWidth / 2
                }
                setPadding(padding, 0, padding, 0)
            } else {
                Log.e("CenterRecyclerView", "Could not get first ViewHolder")
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        updatePadding()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        updatePadding()
    }
}