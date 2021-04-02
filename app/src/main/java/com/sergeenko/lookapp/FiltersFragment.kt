package com.sergeenko.lookapp

import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.Window
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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setStatusBar()
    }
    lateinit var llm: LinearLayoutManager

    private fun setStatusBar() {
        val w: Window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //set status text  light
        }
        w.statusBarColor = Color.TRANSPARENT
    }

    private fun setRV(fileList: List<FilterImage>) {
        viewModel.width = requireActivity().window.decorView.width
        val adapter = viewModel.adapter
        llm = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rv.layoutManager = llm
        binding.rv.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rv)
        adapter.setList(_fileList = fileList)

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val newPosition = currentPosition()
                binding.currentItemList.forEachIndexed { _, view ->
                    view.isSelected = false
                }
                if(newPosition >= 0) {
                    binding.currentItemList[newPosition].isSelected = true

                    binding.filtersList.forEachIndexed { index, view ->
                        val b = FilterViewBinding.bind(view)
                        b.textView.isActivated = thumbs[index].filter == viewModel.adapter.getCurrentFile(newPosition).filter
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun setListeners() {
        setFilters()
        val file = arguments?.getSerializable("files") as List<File>

        binding.toolbarGallary.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        setCurrentListSlider(file.size)

        setRV(file.map { FilterImage(it, null) })

    }

    private fun setCurrentListSlider(size: Int) {
        binding.currentItemList.forEachIndexed { index, view ->
            if(index < size)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.GONE
        }
    }

    override fun <T> manageSuccess(obj: T?) {
        if(obj is String && obj == "Delete"){
            if(viewModel.adapter.fileList.isNotEmpty()){
                setCurrentListSlider(viewModel.adapter.fileList.size)
            }else {
                findNavController().popBackStack()
            }
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
