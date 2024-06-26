package com.dicoding.areunemia.view.scan

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.FragmentEyeUploadBinding
import com.dicoding.areunemia.utils.allPermissionsGranted
import com.dicoding.areunemia.utils.showEyeExampleDialog
import com.dicoding.areunemia.utils.showImageSourceDialog
import com.dicoding.areunemia.utils.showToast
import com.dicoding.areunemia.utils.startCrop
import com.dicoding.areunemia.view.ViewModelFactory
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
                showToast(requireContext(), requireContext().getString(R.string.permission_granted))
            } else {
                showToast(requireContext(), requireContext().getString(R.string.permission_denied))
            }
        }

    private val takePhotoLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { isSuccess ->
            if (isSuccess) {
                showToast(requireContext(), requireContext().getString(R.string.photo_taken))
                Log.e("Picture", requireContext().getString(R.string.photo_taken))
                currentImageUri?.let { uri ->
                    startCrop(this, uri)
                }
            } else {
                showToast(requireContext(), requireContext().getString(R.string.photo_failed))
                currentImageUri = null
                Log.e("Picture", requireContext().getString(R.string.photo_failed))
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                startCrop(this, uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEyeUploadBinding.inflate(inflater, container, false)
        setupAction()
        return binding.root
    }

    private fun setupAction() {
        if (!allPermissionsGranted(requireContext())) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }
        showEyeExampleDialog(this) {
            showImageSourceDialog(this, requestPermissionLauncher, pickImageLauncher, takePhotoLauncher, ::setCurrentImageUri)
        }

        binding.changeImageButton.setOnClickListener {
            showEyeExampleDialog(this) {
                showImageSourceDialog(this, requestPermissionLauncher, pickImageLauncher, takePhotoLauncher, ::setCurrentImageUri)
            }
        }
        binding.continueStepButton.setOnClickListener {
            currentImageUri?.let {
                setCurrentImageUri(currentImageUri!!)
                (activity as? ScanProcessActivity)?.navigateToNextFragment(this)
            } ?: run {
                showToast(requireContext(), getString(R.string.empty_image_warning))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                setCurrentImageUri(resultUri)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("Crop Image MainActivity", "$cropError")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivEyesPhoto.setImageURI(it)
        }
    }

    private fun setCurrentImageUri(uri: Uri) {
        currentImageUri = uri
        showImage()
        scanProcessViewModel.setCurrentImageUri(uri)
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

}