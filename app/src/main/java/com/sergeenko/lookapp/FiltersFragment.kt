package com.sergeenko.lookapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.DeletePhotoBinding
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
    lateinit var llm: CustomGridLayoutManager

    private fun setStatusBar() {
        val w: Window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //set status text  light
        }
        w.statusBarColor = Color.TRANSPARENT
    }

    private fun setRV(fileList: List<FilterImage>) {
        viewModel.width = requireActivity().window.decorView.width
        llm = CustomGridLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val adapter = viewModel.adapter
        binding.rv.layoutManager = llm
        binding.rv.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rv)
        adapter.canScroll = { llm.canScrollHorizontally() }

        if(adapter.fileList.isEmpty()){
            adapter.setList(_fileList = fileList)
        }

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                try {
                    val newPosition = currentPosition()
                    binding.currentItemList.forEachIndexed { _, view ->
                        view.isSelected = false
                    }
                    if (newPosition >= 0) {
                        binding.currentItemList[newPosition].isSelected = true

                        binding.filtersList.forEachIndexed { index, view ->
                            val b = FilterViewBinding.bind(view)
                            b.textView.isActivated =
                                thumbs[index].filter == viewModel.adapter.getCurrentFile(
                                    newPosition
                                ).filter
                        }
                        checkChanges()
                    }
                }catch (e: Exception){

                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun setListeners() {
        binding.settings.post {
            viewModel.setState(SettngsScreenState.Filters)
        }

        val file = arguments?.getSerializable("files") as List<File>

        binding.toolbarGallary.setNavigationOnClickListener {
            if(viewModel.screenState is SettngsScreenState.Filters || viewModel.screenState is SettngsScreenState.Settings) {
                showDeletePopUp(it)
            }else {
                when (viewModel.screenState) {
                    is SettngsScreenState.Orientation -> {
                        viewModel.adapter.clearOrientation(currentPosition())
                    }
                    is SettngsScreenState.Brightness -> {
                        viewModel.adapter.clearBrightness(currentPosition())
                    }
                    is SettngsScreenState.Contrast -> {
                        viewModel.adapter.clearContrast(currentPosition())
                    }
                    is SettngsScreenState.Background -> {
                        viewModel.adapter.clearBackground(currentPosition())
                    }
                }
                viewModel.setState(SettngsScreenState.Settings)
            }
        }

        binding.gallaryText.setOnClickListener {
            viewModel.setState(SettngsScreenState.Filters)
        }

        binding.newPhotoText.setOnClickListener {
            viewModel.setState(SettngsScreenState.Settings)
        }

        binding.trash.setOnClickListener {
            viewModel.delete(currentPosition())
        }

        binding.orientation.setOnClickListener {
            viewModel.setState(SettngsScreenState.Orientation)
        }

        binding.brightness.setOnClickListener {
            viewModel.setState(SettngsScreenState.Brightness)
        }

        binding.contrast.setOnClickListener {
            viewModel.setState(SettngsScreenState.Contrast)
        }

        binding.background.setOnClickListener {
            viewModel.setState(SettngsScreenState.Background)
        }

        binding.nextText.setOnClickListener {
            if(viewModel.screenState is SettngsScreenState.Filters || viewModel.screenState is SettngsScreenState.Settings) {
                findNavController().navigate(
                    R.id.action_filtersFragment_to_finaLookScreenFragment, bundleOf(
                        "filters" to viewModel.adapter.fileList
                    )
                )
            }else {
                when (viewModel.screenState) {
                    is SettngsScreenState.Orientation -> {
                        viewModel.adapter.saveOrientation(currentPosition())
                    }
                    is SettngsScreenState.Brightness -> {
                        viewModel.adapter.saveBrightness(currentPosition())
                    }
                    is SettngsScreenState.Contrast -> {
                        viewModel.adapter.saveContrast(currentPosition())
                    }
                    is SettngsScreenState.Background -> {
                        viewModel.adapter.saveBG(currentPosition())
                    }
                }
                viewModel.setState(SettngsScreenState.Settings)
            }
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
        }else if(obj is SettngsScreenState){
            Log.i("CREENSTATE", obj.toString())
            if(obj is SettngsScreenState.Orientation){
                if(this::llm.isInitialized)
                    llm.setScrollEnabled(false)
                setOrientation()
            }else {
                if(this::llm.isInitialized)
                    llm.setScrollEnabled(true)
                when(obj){
                    is SettngsScreenState.Filters -> setFilters()
                    is SettngsScreenState.Settings -> setSettings()
                    is SettngsScreenState.Brightness -> setBrightness()
                    is SettngsScreenState.Contrast -> setContrast()
                    is SettngsScreenState.Background -> setBackground()
                }
            }
        }
    }

    private fun setFilters() {
        setNormalToolbar()

        if(binding.filtersList.isEmpty()) {
            val filters: List<Filter> = FilterPack.getFilterPack(context)
            val inflater = LayoutInflater.from(context)

            val handler = Handler(Looper.myLooper()!!)
            val r = Runnable {
                val thumbImage = BitmapFactory.decodeResource(resources, R.drawable.look_mock_bg)
                ThumbnailsManager.clearThumbs()

                filters.forEach { filter ->
                    val thumbnailItem = ThumbnailItem()
                    thumbnailItem.image = thumbImage
                    thumbnailItem.filter = filter
                    thumbnailItem.filterName = filter.name
                    ThumbnailsManager.addThumb(thumbnailItem);
                }

                thumbs = ThumbnailsManager.processThumbs(context)
                thumbs.forEach {
                    val img = FilterViewBinding.inflate(inflater, null, false)
                    img.filterImg.setImageBitmap(it.image)
                    img.filterImg.clipToOutline = true
                    img.textView.text = it.filterName
                    img.filterImg.setOnClickListener { _ ->
                        binding.filtersList.forEach { view ->
                            FilterViewBinding.bind(view).textView.isActivated = false
                        }
                        img.textView.isActivated =
                            viewModel.applyFilter(currentPosition(), it.filter)
                    }
                    binding.filtersList.addView(img.root)
                }

            };
            handler.post(r)
        }
        binding.settings.visibility = View.GONE
        binding.filters.visibility = View.VISIBLE

        binding.newPhotoText.isActivated = true
        binding.gallaryText.isActivated = false

    }

    private fun setNormalToolbar() {
        binding.toolbarTitleGallary.text = getString(R.string.new_look)
        binding.nextText.text = getString(R.string.next)

        binding.orientationLayout.visibility = View.GONE
        binding.contrastLayout.visibility = View.GONE
        binding.brightnessLayout.visibility = View.GONE
        binding.backgroundLayout.visibility = View.GONE
        binding.tabs.visibility = View.VISIBLE
    }

    private fun setOrientation(){
        binding.toolbarTitleGallary.text = getString(R.string.orientation)
        binding.nextText.text = getString(R.string.save)

        binding.tabs.visibility = View.GONE

        binding.filters.visibility = View.GONE
        binding.settings.visibility = View.GONE
        binding.orientationLayout.visibility = View.VISIBLE
        binding.brightnessLayout.visibility = View.GONE
        binding.contrastLayout.visibility = View.GONE
        binding.backgroundLayout.visibility = View.GONE
    }

    private fun setBrightness(){
        binding.toolbarTitleGallary.text = getString(R.string.brightness)
        binding.nextText.text = getString(R.string.save)

        binding.tabs.visibility = View.GONE

        binding.filters.visibility = View.GONE
        binding.settings.visibility = View.GONE
        binding.orientationLayout.visibility = View.GONE
        binding.brightnessLayout.visibility = View.VISIBLE
        binding.backgroundLayout.visibility = View.GONE
        binding.contrastLayout.visibility = View.GONE
    }

    private fun setContrast(){
        binding.toolbarTitleGallary.text = getString(R.string.contrast)
        binding.nextText.text = getString(R.string.save)

        binding.tabs.visibility = View.GONE

        binding.filters.visibility = View.GONE
        binding.settings.visibility = View.GONE
        binding.orientationLayout.visibility = View.GONE
        binding.contrastLayout.visibility = View.VISIBLE
        binding.brightnessLayout.visibility = View.GONE
        binding.backgroundLayout.visibility = View.GONE
    }

    private fun setBackground(){
        binding.toolbarTitleGallary.text = getString(R.string.background)
        binding.nextText.text = getString(R.string.save)

        binding.tabs.visibility = View.GONE

        binding.filters.visibility = View.GONE
        binding.settings.visibility = View.GONE
        binding.orientationLayout.visibility = View.GONE
        binding.contrastLayout.visibility = View.GONE
        binding.brightnessLayout.visibility = View.GONE
        binding.backgroundLayout.visibility = View.VISIBLE
    }

    private fun setSettings(){
        setNormalToolbar()

        binding.filters.visibility = View.GONE
        binding.settings.visibility = View.VISIBLE

        binding.newPhotoText.isActivated = false
        binding.gallaryText.isActivated = true

        checkChanges()
    }

    private fun checkChanges() {
        binding.orientationDot.visibility = if(viewModel.adapter.hasOrientationChanges(currentPosition())) View.VISIBLE else View.GONE
        binding.contrastDot.visibility = if(viewModel.adapter.hasContrastChanges(currentPosition())) View.VISIBLE else View.GONE
        binding.bgDot.visibility = if(viewModel.adapter.hasBackgroundChanges(currentPosition())) View.VISIBLE else View.GONE
        binding.brightnessDot.visibility = if(viewModel.adapter.hasBrightnesChanges(currentPosition())) View.VISIBLE else View.GONE
    }

    private fun currentPosition(): Int = (llm.findFirstVisibleItemPosition() + llm.findLastVisibleItemPosition()) / 2

    private fun showDeletePopUp(view: View) {

        val customLayout = DeletePhotoBinding.inflate(LayoutInflater.from(view.context))

        // create an alert builder
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it

        val window = PopupWindow(customLayout.root, width, height, focusable)
        window.isOutsideTouchable = true


        customLayout.yes.setOnClickListener {
            window.dismiss()
            findNavController().popBackStack()
        }

        customLayout.no.setOnClickListener {
            window.dismiss()
        }

        window.contentView = customLayout.root
        window.showAtLocation(view, Gravity.CENTER, 0, 0)
    }
}

class CustomGridLayoutManager(context: Context?, horizontal: Int, b: Boolean) : LinearLayoutManager(context, horizontal, b) {
    private var isScrollEnabled = true

    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    override fun canScrollHorizontally():  Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollHorizontally()
    }
}