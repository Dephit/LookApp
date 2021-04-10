package com.sergeenko.lookapp.fragments


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.R
import com.sergeenko.lookapp.adapters.ColorAdapter
import com.sergeenko.lookapp.adapters.FilterImage
import com.sergeenko.lookapp.adapters.RotationMode
import com.sergeenko.lookapp.databinding.DeletePhotoBinding
import com.sergeenko.lookapp.databinding.FilterViewBinding
import com.sergeenko.lookapp.databinding.FiltersFragmentBinding
import com.sergeenko.lookapp.viewModels.FiltersViewModel
import com.sergeenko.lookapp.viewModels.SettngsScreenState
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


@AndroidEntryPoint
class FiltersFragment : BaseFragment<FiltersFragmentBinding>() {
    override val viewModel: FiltersViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): FiltersFragmentBinding = FiltersFragmentBinding.inflate(inflater)

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
        viewModel.adapter.width = requireActivity().window.decorView.width
        llm = CustomGridLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val adapter = viewModel.adapter
        binding.rv.layoutManager = llm
        binding.rv.adapter = adapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rv)
        adapter.canScroll = { viewModel.screenState != SettngsScreenState.Orientation }

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
                        val currentFile = viewModel.adapter.getCurrentFile(newPosition)
                        binding.currentItemList[newPosition].isSelected = true

                        currentFile.backgroundColor?.let {
                            binding.rv.setBackgroundColor(Color.parseColor(it))
                            binding.imgLayout.setBackgroundColor(Color.parseColor(it))
                        } ?: run {
                            binding.rv.setBackgroundColor(Color.parseColor(currentFile.oldBackgroundColor))
                            binding.imgLayout.setBackgroundColor(Color.parseColor(currentFile.oldBackgroundColor))
                        }

                        (binding.colorRv.adapter as ColorAdapter).updateSelectedColor(currentFile.backgroundColor)

                        binding.filtersList.forEachIndexed { index, view ->
                            val b = FilterViewBinding.bind(view)
                            b.textView.isActivated =
                                    viewModel.thumbs[index].filter == currentFile.filter
                        }
                        checkChanges()
                    }
                } catch (e: Exception) {

                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    override fun setListeners() {
        binding.settings.post {
            viewModel.setState(SettngsScreenState.Filters)
        }

        val file = arguments?.getParcelableArrayList<Uri>("files")

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
                lifecycleScope.launch {
                    binding.loading.visibility = View.VISIBLE
                    val list = mutableListOf<Bitmap?>()
                    viewModel.adapter.fileList.forEachIndexed { index, filterImage ->
                        binding.rv.scrollToPosition(index)
                        delay(50)
                        list.add(loadBitmapFromView(binding.rv))
                    }
                    findNavController().navigate(
                            R.id.action_filtersFragment_to_finaLookScreenFragment, bundleOf(
                            "filters" to list
                    )
                    )
                    binding.loading.visibility = View.GONE
                }
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

        binding.rotationX.setOnClickListener {
            setRotationMode(RotationMode.RotationX)
            binding.rotationXText.text = viewModel.getCurrentRotation(currentPosition())

            binding.rotationXOpen.visibility = View.VISIBLE
            binding.rotationXClosed.visibility = View.GONE

            binding.rotationZOpen.visibility = View.GONE
            binding.rotationZClosed.visibility = View.VISIBLE

            binding.rotationYOpen.visibility = View.GONE
            binding.rotationYClosed.visibility = View.VISIBLE
        }

        binding.closeRotation.setOnClickListener {
            setRotationMode(RotationMode.None)
            binding.rotationXOpen.visibility = View.GONE
            binding.rotationXClosed.visibility = View.VISIBLE
        }

        binding.rotationY.setOnClickListener {
            setRotationMode(RotationMode.RotationY)
            binding.rotationYText.text = viewModel.getCurrentRotation(currentPosition())

            binding.rotationXOpen.visibility = View.GONE
            binding.rotationXClosed.visibility = View.VISIBLE

            binding.rotationZOpen.visibility = View.GONE
            binding.rotationZClosed.visibility = View.VISIBLE

            binding.rotationYOpen.visibility = View.VISIBLE
            binding.rotationYClosed.visibility = View.GONE
        }

        binding.closeRotationY.setOnClickListener {
            setRotationMode(RotationMode.None)
            binding.rotationYOpen.visibility = View.GONE
            binding.rotationYClosed.visibility = View.VISIBLE
        }

        binding.rotationZ.setOnClickListener {
            setRotationMode(RotationMode.RotationZ)
            binding.rotationZText.text = viewModel.getCurrentRotation(currentPosition())

            binding.rotationXOpen.visibility = View.GONE
            binding.rotationXClosed.visibility = View.VISIBLE

            binding.rotationZOpen.visibility = View.VISIBLE
            binding.rotationZClosed.visibility = View.GONE

            binding.rotationYOpen.visibility = View.GONE
            binding.rotationYClosed.visibility = View.VISIBLE
        }

        binding.closeRotationZ.setOnClickListener {
            setRotationMode(RotationMode.None)
            binding.rotationZOpen.visibility = View.GONE
            binding.rotationZClosed.visibility = View.VISIBLE
        }

        binding.slider.addOnChangeListener { _, value, _ ->
            val progressWidth = binding.slider.width / 200 * (abs(value) - 1)
            val image9Width = (binding.imageView9.x + (binding.imageView9.width / 2))

            binding.imageView10.updateLayoutParams {
                val w = (progressWidth).toInt()
                width = if(w !in -1..1) w else 1
            }
            binding.imageView10.x = if (value < 0) image9Width - (progressWidth).toInt() else image9Width

            lifecycleScope.launch {
                viewModel.adapter.addBrightness(currentPosition(), value.toInt())
            }

            binding.brightnessValueText.text = value.toInt().toString()
        }

        binding.sliderContrast.addOnChangeListener { _, value, _ ->
            val progressWidth = binding.sliderContrast.width / 200 * (abs(value - 50) * 2 - 1)
            val image9Width = (binding.imageView11.x + (binding.imageView11.width / 2))

            binding.imageView12.updateLayoutParams {
                val w = (progressWidth).toInt()
                width = if(w !in -1..1) w else 1
            }
            binding.imageView12.x = if (value < 50) image9Width - (progressWidth).toInt() else image9Width

            lifecycleScope.launch {
                viewModel.adapter.addContrast(currentPosition(),
                        when {
                            value <= 50 -> value / 50
                            value <= 60 -> value / 40
                            value <= 70 -> value / 30
                            value <= 80 -> value / 20
                            else -> value / 10
                        }

                )
            }

            binding.contrastValueText.text = value.toInt().toString()
        }

        setCurrentListSlider(file!!.size)

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
                when(obj){
                    is SettngsScreenState.Filters -> {
                        if(this::llm.isInitialized)
                            llm.setScrollEnabled(true)
                        setFilters()
                    }
                    is SettngsScreenState.Settings -> {
                        if(this::llm.isInitialized)
                            llm.setScrollEnabled(true)
                        setSettings()
                    }
                    is SettngsScreenState.Brightness -> {
                        if(this::llm.isInitialized)
                            llm.setScrollEnabled(false)
                        setBrightness()
                    }
                    is SettngsScreenState.Contrast -> {
                        if(this::llm.isInitialized)
                            llm.setScrollEnabled(false)
                        setContrast()
                    }
                    is SettngsScreenState.Background -> {
                        if(this::llm.isInitialized)
                            llm.setScrollEnabled(false)
                        setBackground()
                    }
                }
            }
        }
    }

    private fun setFilters() {
        setNormalToolbar()

        if(binding.filtersList.isEmpty()) {
            val inflater = LayoutInflater.from(context)
            val thumbImage = BitmapFactory.decodeResource(resources, R.drawable.look_mock_bg)

            lifecycleScope.launch {
                ThumbnailsManager.clearThumbs()

                FilterPack.getFilterPack(context).forEach {
                    ThumbnailsManager.addThumb(ThumbnailItem().apply {
                                image = thumbImage
                                filter = it
                                filterName = it.name
                            })
                }

                viewModel.thumbs = ThumbnailsManager.processThumbs(context)
                viewModel.thumbs.forEach {
                    binding.filtersList.addView(bindFilterViewBinding(inflater, it).root)
                }
            }
        }
        binding.settings.visibility = View.GONE
        binding.filters.visibility = View.VISIBLE

        binding.newPhotoText.isActivated = true
        binding.gallaryText.isActivated = false

    }

    private fun bindFilterViewBinding(inflater: LayoutInflater, it: ThumbnailItem): FilterViewBinding {
        val viewBinding = FilterViewBinding.inflate(inflater, null, false)
        viewBinding.filterImg.setImageBitmap(it.image)
        viewBinding.filterImg.clipToOutline = true
        viewBinding.textView.text = it.filterName
        viewBinding.filterImg.setOnClickListener { _ ->
            binding.filtersList.forEach { view ->
                FilterViewBinding.bind(view).textView.isActivated = false
            }
            viewBinding.textView.isActivated =
                    viewModel.applyFilter(currentPosition(), it.filter)
        }
        return viewBinding
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
        if(binding.orientationRv.isEmpty()){
            setOrientationRv()
        }

        binding.rotationXOpen.visibility = View.GONE
        binding.rotationXClosed.visibility = View.VISIBLE

        binding.rotationZOpen.visibility = View.GONE
        binding.rotationZClosed.visibility = View.VISIBLE

        binding.rotationYOpen.visibility = View.GONE
        binding.rotationYClosed.visibility = View.VISIBLE

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

    private fun setOrientationRv() {
        val adapter = viewModel.orientationAdapter
        binding.orientationRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.orientationRv.adapter = adapter
        binding.orientationRv.scrollToPosition(5000)

        binding.orientationRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (viewModel.screenState == SettngsScreenState.Orientation) {
                    lifecycleScope.launch {
                        if (viewModel.adapter.rotationMode == RotationMode.RotationX) {
                            binding.rotationXText.text = if (dx > 0) {
                                viewModel.adapter.rotate(currentPosition(), 2.5f).toString()
                            } else {
                                viewModel.adapter.rotate(currentPosition(), -2.5f).toString()
                            }
                        } else if (viewModel.adapter.rotationMode == RotationMode.RotationY) {
                            binding.rotationYText.text = if (dx > 0) {
                                viewModel.adapter.rotateY(currentPosition(), 2.5f).toString()
                            } else {
                                viewModel.adapter.rotateY(currentPosition(), -2.5f).toString()
                            }
                        } else if (viewModel.adapter.rotationMode == RotationMode.RotationZ) {
                            binding.rotationZText.text = if (dx > 0) {
                                viewModel.adapter.rotateZ(currentPosition(), 2.5f).toString()
                            } else {
                                viewModel.adapter.rotateZ(currentPosition(), -2.5f).toString()
                            }
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun setBrightness(){
        binding.slider.value = viewModel.adapter.getCurrentFile(currentPosition()).brightness.toFloat()
        binding.brightnessValueText.text = viewModel.adapter.getCurrentFile(currentPosition()).brightness.toString()

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
        val value = viewModel.adapter.getCurrentFile(currentPosition()).contrast
        val v2 = when {
            value * 50 <= 50 -> value * 50
            value * 40 <= 60 -> value * 40
            value * 30 <= 70 -> value * 30
            value * 20 <= 80 -> value * 20
            else -> value * 10
        }
        binding.sliderContrast.value = v2
        binding.contrastValueText.text = v2.toInt().toString()

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

        if(binding.colorRv.isEmpty()){
            setColorRv()
        }

        binding.tabs.visibility = View.GONE

        binding.filters.visibility = View.GONE
        binding.settings.visibility = View.GONE
        binding.orientationLayout.visibility = View.GONE
        binding.contrastLayout.visibility = View.GONE
        binding.brightnessLayout.visibility = View.GONE
        binding.backgroundLayout.visibility = View.VISIBLE
    }

    private fun setColorRv() {
        val adapter = ColorAdapter{ color: String ->
            lifecycleScope.launch {
                val parsedColor = Color.parseColor(color)
                viewModel.adapter.setBackground(currentPosition(), color)
                binding.rv.setBackgroundColor(parsedColor)
                binding.imgLayout.setBackgroundColor(parsedColor)
            }
        }
        binding.colorRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.colorRv.adapter = adapter
    }

    private fun setSettings(){
        setRotationMode(RotationMode.None)

        setNormalToolbar()

        binding.filters.visibility = View.GONE
        binding.settings.visibility = View.VISIBLE

        binding.newPhotoText.isActivated = false
        binding.gallaryText.isActivated = true

        checkChanges()
    }

    private fun setRotationMode(none: RotationMode) {
        viewModel.adapter.rotationMode = none
        if(none is RotationMode.None){
            binding.orientationRv.visibility = View.GONE
            binding.imageView6.visibility = View.GONE
            binding.imageView7.visibility = View.GONE
        }else{
            binding.orientationRv.visibility = View.VISIBLE
            binding.imageView6.visibility = View.VISIBLE
            binding.imageView7.visibility = View.VISIBLE
        }
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

    private fun loadBitmapFromView(v: View): Bitmap? {
        return try{
            val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            v.layout(v.left, v.top, v.right, v.bottom)
            v.draw(c)
            b
        }catch (e: Exception){
            Log.e("Bitmap_error", e.message.toString())
            null
         }
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