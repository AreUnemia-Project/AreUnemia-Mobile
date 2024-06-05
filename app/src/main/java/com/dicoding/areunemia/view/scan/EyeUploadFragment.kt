package com.dicoding.areunemia.view.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.FragmentEyeUploadBinding
import com.dicoding.areunemia.view.ViewModelFactory
import com.dicoding.areunemia.view.helper.*
import com.yalantis.ucrop.UCrop

class EyeUploadFragment : Fragment() {

    private lateinit var binding: FragmentEyeUploadBinding
    private var currentImageUri: Uri? = null
    private val scanProcessViewModel by activityViewModels<ScanProcessViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEyeUploadBinding.inflate(inflater, container, false)
        setupAction()
        return binding.root
    }

    private fun setupAction() {
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        showImageSourceDialog()

        binding.changeImageButton.setOnClickListener {
            showImageSourceDialog()
        }
        binding.continueStepButton.setOnClickListener {
            currentImageUri?.let {
                setCurrentImageUri(currentImageUri!!)
                (activity as? ScanProcessActivity)?.navigateToNextFragment(this)
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }
    }

    private fun showImageSourceDialog() {
        val optionsArray = resources.getStringArray(R.array.image_source_options)
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.select_image_source)
            .setItems(optionsArray) { _, which ->
                when (which) {
                    0 -> pickImageFromGallery()
                    1 -> {
                        if (allPermissionsGranted()) {
                            takePhotoFromCamera()
                        }
                        else {
                            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
                        }
                    }

                }
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun takePhotoFromCamera() {
        currentImageUri = context?.let { getImageUri(it) }
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { isSuccess ->
            if (isSuccess) {
                currentImageUri?.let { uri ->
                    startCrop(uri)
                }
            }
        }

    private fun pickImageFromGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private val launcherGallery =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                startCrop(uri)
            } else {
                Log.d("Photo Picker", "No media selected")
            }
        }

    private fun startCrop(uri: Uri) {
        UCrop.of(uri, Uri.fromFile(requireContext().cacheDir.resolve("${System.nanoTime()}.jpg")))
            .withAspectRatio(16F, 16F)
            .withMaxResultSize(2400, 2400)
            .start(requireContext(), this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            currentImageUri = resultUri
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("Crop Image MainActivity", "$cropError")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivEyesPhoto.setImageURI(it)
        }
    }

    private fun setCurrentImageUri(uri: Uri) {
        scanProcessViewModel.setCurrentImageUri(uri)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

}