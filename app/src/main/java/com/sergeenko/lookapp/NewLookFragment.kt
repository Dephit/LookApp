
package com.sergeenko.lookapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sergeenko.lookapp.databinding.NewLookFragmentBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class NewLookFragment : BaseFragment<NewLookFragmentBinding>() {

    private lateinit var outputDirectory: File

    override val viewModel: NewLookViewModel by viewModels()

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun bind(inflater: LayoutInflater): NewLookFragmentBinding = NewLookFragmentBinding.inflate(inflater)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        outputDirectory = getOutputDirectory()
    }

    private fun setStatusBar() {
        val w: Window = requireActivity().window
        w.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        w.statusBarColor = Color.TRANSPARENT
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            w.decorView.systemUiVisibility = w.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() //set status text  light
        }
    }

    fun setStatusBarBlack() {
        val w: Window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //set status text  light
        }
        w.statusBarColor = Color.TRANSPARENT
    }

    override fun <T> manageSuccess(obj: T?) {
        when (obj) {
            is Int -> {
                setNextVisibility(obj)
            }
            is Boolean -> {
                findNavController().navigate(R.id.action_newLookFragment_to_filtersFragment, bundleOf("files" to viewModel.selectedList))
                ProcessCameraProvider.getInstance(requireContext()).get().unbindAll()
                viewModel.restoreState()
            }
            is List<*> -> {
                setRV(obj as List<GallaryImage>)
            }
            is Uri -> {
                binding.previewImage.visibility = View.GONE
                binding.progressBar4.visibility = View.VISIBLE
                Picasso.get()
                        .load(obj)
                        .noPlaceholder()
                        .into(binding.previewImage, object : Callback {
                            override fun onSuccess() {
                                binding.previewImage.visibility = View.VISIBLE
                                binding.progressBar4.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {

                            }

                        })
            }
        }
    }

    private fun setNextVisibility(obj: Int) {
        binding.nextText.visibility = if(obj > 0) View.VISIBLE else View.GONE
    }

    @SuppressLint("MissingPermission")
    private fun startCamera() {
            try {
                viewModel.isCameraSelected = true
                binding.camera.bindToLifecycle(this)
            } catch (e: Exception) {

            }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case

        // Create time-stamped output file to hold the image
        val imgName = "${SimpleDateFormat(FILENAME_FORMAT, Locale.US
        ).format(System.currentTimeMillis())}.jpg"
        val photoFile = File(outputDirectory, imgName)

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        if(viewModel.isAutoTorchOn)
            binding.camera.enableTorch(true)

        binding.camera.takePicture(
                outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                if(viewModel.isAutoTorchOn)
                    binding.camera.enableTorch(false)
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                if(viewModel.isAutoTorchOn)
                    binding.camera.enableTorch(false)
                val savedUri = Uri.fromFile(photoFile)
                val msg = "Photo capture succeeded: $savedUri"
                galleryAddPic(savedUri)
                viewModel.onImageAdd(GallaryImage(photoFile, true))
                viewModel.savePhoto()
                Log.d(TAG, msg)
            }
        })

    }

    private fun galleryAddPic(contentUri: Uri) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

        mediaScanIntent.data = contentUri
        context?.sendBroadcast(mediaScanIntent)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults:
            IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                showToast("Permissions not granted by the user.")
                findNavController().popBackStack()
            }
        }
    }

    override fun setListeners() {
        withBinding {
            if(viewModel.isCameraSelected)
                setCameraSelected()
            else
                setGallerySelected()

             var xCoOrdinate: Float? = null
             var yCoOrdinate: Float? = null

            previewImage.setOnTouchListener(View.OnTouchListener { view, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        xCoOrdinate = view.x - event.rawX
                        yCoOrdinate = view.y - event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> view.animate()
                        .x(event.rawX + xCoOrdinate!!)
                        .y(event.rawY + yCoOrdinate!!).setDuration(0).start()
                    else -> return@OnTouchListener false
                }
                true
            })

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            toolbarGallary.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            gallaryText.setOnClickListener {
                setGallerySelected()
            }

            newPhotoText.setOnClickListener {
                setCameraSelected()
            }

            toggleLightning.setOnClickListener {
                if(!viewModel.isAutoTorchOn && !camera.isTorchOn){
                    viewModel.isAutoTorchOn = true
                    it.isActivated = false
                    it.isSelected = true
                    camera.enableTorch(false)
                }else if(!camera.isTorchOn){
                    viewModel.isAutoTorchOn = false
                    it.isActivated = true
                    it.isSelected = false
                    camera.enableTorch(true)
                }else {
                    viewModel.isAutoTorchOn = false
                    it.isActivated = false
                    it.isSelected = false
                    camera.enableTorch(false)
                }
            }

            shot.setOnClickListener {
                takePhoto()
            }

            reverseCamera.setOnClickListener {
                camera.toggleCamera()
            }

            nextText.setOnClickListener {
                viewModel.savePhoto()
            }
        }
    }

    private fun setGallerySelected() {
        withBinding {
            setStatusBarBlack()
            setNextVisibility(viewModel.selectedList.size)
            gallaryText.isActivated = false
            newPhotoText.isActivated = true
            viewModel.isCameraSelected = false

            fromGallary.visibility = View.VISIBLE
            fromCamera.visibility = View.GONE

            if(viewModel.fileList.isEmpty()){
                viewModel.loadSavedImages()
            }else {
                setRV(viewModel.fileList)
            }
        }
    }

    private fun setRV(fileList: List<GallaryImage>) {
        val adapter = viewModel.adapter
        adapter.setList(_fileList = fileList)
        binding.rv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.rv.adapter = adapter
    }

    private fun setCameraSelected() {
        withBinding{
            setStatusBar()
            gallaryText.isActivated = true
            newPhotoText.isActivated = false
            viewModel.isCameraSelected = true

            fromGallary.visibility = View.GONE
            fromCamera.visibility = View.VISIBLE
        }
    }

}