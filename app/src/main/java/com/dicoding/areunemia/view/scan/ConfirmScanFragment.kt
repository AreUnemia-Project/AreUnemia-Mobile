package com.dicoding.areunemia.view.scan

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.CustomAlertTitleBinding
import com.dicoding.areunemia.databinding.FragmentConfirmScanBinding
import com.dicoding.areunemia.utils.*
import com.dicoding.areunemia.view.ViewModelFactory
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class ConfirmScanFragment : Fragment() {

    private lateinit var binding: FragmentConfirmScanBinding
    private val scanProcessViewModel by activityViewModels<ScanProcessViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(requireContext(), "Permission request granted")
            } else {
                showToast(requireContext(), "Permission request denied")
            }
        }

    private val takePhotoLauncher =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { isSuccess ->
            if (isSuccess) {
                currentImageUri?.let { uri ->
                    startCrop(this, uri)
                }
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            if (uri != null) {
                startCrop(this, uri)
            } else {
                Log.d("Photo Picker", "No media selected")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmScanBinding.inflate(inflater, container, false)
        setupView()
        setupAction()
        observeViewModel()
        return binding.root
    }

    private fun setupView() {

    }

    private fun setupAction() {
        binding.ivEyesPhoto.setOnClickListener{
            showEyeExampleDialog(this) {
                showImageSourceDialog(this, requestPermissionLauncher, pickImageLauncher, takePhotoLauncher, ::setCurrentImageUri)
            }
        }
        binding.apply {
            val questionArray = resources.getStringArray(R.array.question_array)
            buttonQuestionOne.setOnClickListener { updateAnswer(1, questionArray[0]) }
            buttonQuestionTwo.setOnClickListener { updateAnswer(2, questionArray[1]) }
            buttonQuestionThree.setOnClickListener { updateAnswer(3, questionArray[2]) }
            buttonQuestionFour.setOnClickListener { updateAnswer(4, questionArray[3]) }
            buttonQuestionFive.setOnClickListener { updateAnswer(5, questionArray[4]) }
            buttonQuestionSix.setOnClickListener { updateAnswer(6, questionArray[5]) }
            buttonQuestionSeven.setOnClickListener { updateAnswer(7, questionArray[6]) }
            buttonQuestionEight.setOnClickListener { updateAnswer(8, questionArray[7]) }
            buttonQuestionNine.setOnClickListener { updateAnswer(9, questionArray[8]) }
            buttonQuestionTen.setOnClickListener { updateAnswer(10, questionArray[9]) }

            continueConfirmButton.setOnClickListener {
                showConfirmationDialog(requireContext(), getString(R.string.confirm_submit_scan)) {
                    submitScan()
                }
            }
        }
    }

    private fun observeViewModel() {
        scanProcessViewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                showImage(it)
            }
        }

        scanProcessViewModel.answers.observe(viewLifecycleOwner) { answers ->
            answers?.let {
                binding.apply {
                    // Update button colors based on answers
                    setButtonTint(binding.buttonQuestionOne, getButtonColor(answers[0]))
                    setButtonTint(binding.buttonQuestionTwo, getButtonColor(answers[1]))
                    setButtonTint(binding.buttonQuestionThree, getButtonColor(answers[2]))
                    setButtonTint(binding.buttonQuestionFour, getButtonColor(answers[3]))
                    setButtonTint(binding.buttonQuestionFive, getButtonColor(answers[4]))
                    setButtonTint(binding.buttonQuestionSix, getButtonColor(answers[5]))
                    setButtonTint(binding.buttonQuestionSeven, getButtonColor(answers[6]))
                    setButtonTint(binding.buttonQuestionEight, getButtonColor(answers[7]))
                    setButtonTint(binding.buttonQuestionNine, getButtonColor(answers[8]))
                    setButtonTint(binding.buttonQuestionTen, getButtonColor(answers[9]))
                }
            }
        }

        scanProcessViewModel.uploadResult.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                if (it.status == "success") {
                    it.data?.result?.let { it1 ->
                        val intent = Intent(requireContext(), ScanResultsActivity::class.java)
                        intent.putExtra("result", it1)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                } else {
                    showErrorDialog(requireContext(), getString(R.string.error_message))
                }
            }
        })

        scanProcessViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

    }

    private fun showImage(uri: Uri) {
        binding.ivEyesPhoto.setImageURI(uri)
    }

    private fun getButtonColor(answer: Int): Int {
        return if (answer == 1) {
            ContextCompat.getColor(requireContext(), R.color.green)
        } else {
            ContextCompat.getColor(requireContext(), R.color.orange)
        }
    }

    private fun setButtonTint(button: Button, color: Int) {
        button.backgroundTintList = ColorStateList.valueOf(color)
    }

    private fun updateAnswer(questionNumber: Int, questionText: String) {
        val optionsArray = resources.getStringArray(R.array.answer_options)
        val dialogBuilder = AlertDialog.Builder(requireContext())

        val alertTitleBinding = CustomAlertTitleBinding.inflate(layoutInflater)
        alertTitleBinding.customQuestionTitle.text = getString(R.string.question_title, questionNumber)
        alertTitleBinding.customAlertTitle.text = questionText
        dialogBuilder.setCustomTitle(alertTitleBinding.root)

        var selectedAnswer: Int? = null
        val currentAnswer = scanProcessViewModel.answers.value?.getOrNull(questionNumber - 1)

        dialogBuilder.setSingleChoiceItems(optionsArray, if (currentAnswer == 1) 0 else 1) { _, which ->
            selectedAnswer = if (which == 0) 1 else 0
        }

        dialogBuilder.setPositiveButton(R.string.confirm) { dialog, _ ->
            selectedAnswer?.let {
                scanProcessViewModel.updateAnswer(questionNumber - 1, it)
            }
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
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

    private fun setCurrentImageUri(uri: Uri) {
        currentImageUri = uri
        scanProcessViewModel.setCurrentImageUri(uri)
        showImage(uri)
    }

    private fun submitScan() {
        val answersList = scanProcessViewModel.answers.value
        val eyePhotoUri = scanProcessViewModel.currentImageUri.value

        if (answersList != null && eyePhotoUri != null) {

            val eyePhotoFile = eyePhotoUri.let {
                uriToFile(it, requireContext()).reduceFileImage()
            }
            val requestImageFile = eyePhotoFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = requestImageFile.let {
                MultipartBody.Part.createFormData(
                    "photo",
                    eyePhotoFile.name,
                    it
                )
            }

            val questionnaireAnswers = JSONObject().apply {
                answersList.forEachIndexed { index, answer ->
                    val answerText = if (answer == 1) "Yes" else "No"
                    put("question${index + 1}", answerText)
                }
            }

            val answersBody = questionnaireAnswers.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            scanProcessViewModel.uploadScan(multipartBody, answersBody, requireContext())

        } else {
            showToast(requireContext(), "Please provide all necessary information")
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

}