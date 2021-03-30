
package com.sergeenko.lookapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.camera.core.*
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class NewLookFragment : BaseFragment<NewLookFragmentBinding>() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override val viewModel: NewLookViewModel by viewModels()

    override fun bind(inflater: LayoutInflater): NewLookFragmentBinding = NewLookFragmentBinding.inflate(inflater)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (allPermissionsGranted()) {
            //startCamera()
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun <T> manageSuccess(obj: T?) {
        when (obj) {
            is Boolean -> {
                findNavController().navigate(R.id.action_newLookFragment_to_filtersFragment, bundleOf("files" to viewModel.selectedList))
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

    @SuppressLint("MissingPermission")
    private fun startCamera() {
        binding.camera.bindToLifecycle(this)
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
        binding.camera.takePicture(
                outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
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

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        //CameraX.unbindAll()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun setListeners() {
        withBinding {
            setCameraSelected()
            
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
                val torch = !camera.isTorchOn
                it.isActivated = torch
                camera.enableTorch(torch)
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
            gallaryText.isActivated = false
            newPhotoText.isActivated = true

            fromGallary.visibility = View.VISIBLE
            fromCamera.visibility = View.GONE

            if(viewModel.fileList.isEmpty()){
                viewModel.loadSavedImages()
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
        withBinding {
            gallaryText.isActivated = true
            newPhotoText.isActivated = false

            fromGallary.visibility = View.GONE
            fromCamera.visibility = View.VISIBLE
        }
    }

}