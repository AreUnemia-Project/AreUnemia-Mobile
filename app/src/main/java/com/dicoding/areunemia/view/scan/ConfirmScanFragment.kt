package com.dicoding.areunemia.view.scan

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.CustomAlertTitleBinding
import com.dicoding.areunemia.databinding.FragmentConfirmScanBinding
import com.dicoding.areunemia.view.ViewModelFactory

class ConfirmScanFragment : Fragment() {

    private lateinit var binding: FragmentConfirmScanBinding
    private val scanProcessViewModel by activityViewModels<ScanProcessViewModel> {
        ViewModelFactory.getInstance(requireContext())
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

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//    }

    private fun setupView() {

    }

    private fun observeViewModel() {
        Log.e("ConfirmScanFragment", "SBLM OBSERVE CURRENT IMAGE")
        scanProcessViewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                Log.e("ConfirmScanFragment", "MASUK OBSERVE CURRENT IMAGE")
                binding.ivEyesPhoto.setImageURI(uri)
            }
            Log.e("ConfirmScanFragment", "KELUAR OBSERVE CURRENT IMAGE")
        }

        scanProcessViewModel.answers.observe(viewLifecycleOwner) { answers ->
            answers?.let {
                // Update UI with the answers
                binding.apply {
                    // Update button colors based on answers
                    Log.e("ConfirmScanFragment", answers.toString())
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
    }

    private fun getButtonColor(answer: Int): Int {
        // Return the color based on the answer (for example: green for yes, orange for no)
        return if (answer == 1) {
            // Return green color
            ContextCompat.getColor(requireContext(), R.color.green)
        } else {
            // Return orange color
            ContextCompat.getColor(requireContext(), R.color.orange)
        }
    }

    private fun setupAction() {
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
        }
    }

    private fun setButtonTint(button: Button, color: Int) {
        button.backgroundTintList = ColorStateList.valueOf(color)
    }

    private fun updateAnswer(questionNumber: Int, questionText: String) {
        val optionsArray = resources.getStringArray(R.array.answer_options)
        val dialogBuilder = AlertDialog.Builder(requireContext())

        // Inflate the custom layout using view binding
        val alertTitleBinding = CustomAlertTitleBinding.inflate(layoutInflater)

        // Set the question title and question text
        alertTitleBinding.customQuestionTitle.text = getString(R.string.question_title, questionNumber)
        alertTitleBinding.customAlertTitle.text = questionText

        // Use the custom alert title's root view
        dialogBuilder.setCustomTitle(alertTitleBinding.root)

        var selectedAnswer: Int? = null

        dialogBuilder.setSingleChoiceItems(optionsArray, -1) { _, which ->
            selectedAnswer = if (which == 0) 1 else 0 // Map index 0 to answer 1 (Yes), index 1 to answer 0 (No)
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






}