package com.dicoding.areunemia.view.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dicoding.areunemia.R
import com.dicoding.areunemia.databinding.FragmentEyeUploadBinding
import com.dicoding.areunemia.databinding.FragmentQuestionnaireBinding
import com.dicoding.areunemia.utils.showToast
import com.dicoding.areunemia.view.ViewModelFactory

class QuestionnaireFragment : Fragment() {

    private lateinit var binding: FragmentQuestionnaireBinding
    private val scanProcessViewModel by activityViewModels<ScanProcessViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentQuestionnaireBinding.inflate(inflater, container, false)
        setupAction()
        return binding.root

    }

    private fun setupAction() {
        binding.continueStepButton.setOnClickListener {
            if (validateAnswers()) {
                val answers = mutableListOf<Int>()
                answers.add(getSelectedAnswer(binding.rgQuestionOne))
                answers.add(getSelectedAnswer(binding.rgQuestionTwo))
                answers.add(getSelectedAnswer(binding.rgQuestionThree))
                answers.add(getSelectedAnswer(binding.rgQuestionFour))
                answers.add(getSelectedAnswer(binding.rgQuestionFive))
                answers.add(getSelectedAnswer(binding.rgQuestionSix))
                answers.add(getSelectedAnswer(binding.rgQuestionSeven))
                answers.add(getSelectedAnswer(binding.rgQuestionEight))
                answers.add(getSelectedAnswer(binding.rgQuestionNine))
                answers.add(getSelectedAnswer(binding.rgQuestionTen))

                setAnswers(answers)
                (activity as? ScanProcessActivity)?.navigateToNextFragment(this)
            }
        }
    }

    private fun getSelectedAnswer(radioGroup: RadioGroup): Int {
        return when (radioGroup.checkedRadioButtonId) {
            // For AnswerItem 1
            R.id.rbQuestionOneYes -> 1
            R.id.rbQuestionOneNo -> 0

            // For AnswerItem 2
            R.id.rbQuestionTwoYes -> 1
            R.id.rbQuestionTwoNo -> 0

            // For AnswerItem 3
            R.id.rbQuestionThreeYes -> 1
            R.id.rbQuestionThreeNo -> 0

            // For AnswerItem 4
            R.id.rbQuestionFourYes -> 1
            R.id.rbQuestionFourNo -> 0

            // For AnswerItem 5
            R.id.rbQuestionFiveYes -> 1
            R.id.rbQuestionFiveNo -> 0

            // For AnswerItem 6
            R.id.rbQuestionSixYes -> 1
            R.id.rbQuestionSixNo -> 0

            // For AnswerItem 7
            R.id.rbQuestionSevenYes -> 1
            R.id.rbQuestionSevenNo -> 0

            // For AnswerItem 8
            R.id.rbQuestionEightYes -> 1
            R.id.rbQuestionEightNo -> 0

            // For AnswerItem 9
            R.id.rbQuestionNineYes -> 1
            R.id.rbQuestionNineNo -> 0

            // For AnswerItem 10
            R.id.rbQuestionTenYes -> 1
            R.id.rbQuestionTenNo -> 0

            else -> -1 // Return a default value if none is selected
        }
    }


    private fun validateAnswers(): Boolean {
        val questionOneAnswered = binding.rgQuestionOne.checkedRadioButtonId != -1
        val questionTwoAnswered = binding.rgQuestionTwo.checkedRadioButtonId != -1
        val questionThreeAnswered = binding.rgQuestionThree.checkedRadioButtonId != -1
        val questionFourAnswered = binding.rgQuestionFour.checkedRadioButtonId != -1
        val questionFiveAnswered = binding.rgQuestionFive.checkedRadioButtonId != -1
        val questionSixAnswered = binding.rgQuestionSix.checkedRadioButtonId != -1
        val questionSevenAnswered = binding.rgQuestionSeven.checkedRadioButtonId != -1
        val questionEightAnswered = binding.rgQuestionEight.checkedRadioButtonId != -1
        val questionNineAnswered = binding.rgQuestionNine.checkedRadioButtonId != -1
        val questionTenAnswered = binding.rgQuestionTen.checkedRadioButtonId != -1

        if (!questionOneAnswered || !questionTwoAnswered || !questionThreeAnswered || !questionFourAnswered
            || !questionFiveAnswered || !questionSixAnswered || !questionSevenAnswered || !questionEightAnswered
            || !questionNineAnswered || !questionTenAnswered) {
            showToast(requireContext(), "Please answer all the questions.")
            return false
        }

        return true

    }

    private fun setAnswers(answers: List<Int>) {
        scanProcessViewModel.setAnswers(answers)
    }

}