package com.dicoding.areunemia.view.scan

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.data.local.repository.UserRepository

class ScanProcessViewModel(private val repository: UserRepository) : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri>()
    val currentImageUri: LiveData<Uri> = _currentImageUri

    private val _answers = MutableLiveData<List<Int>>()
    val answers: LiveData<List<Int>> = _answers

    fun setCurrentImageUri(uri: Uri) {
        _currentImageUri.value = uri
    }

    fun setAnswers(answersList: List<Int>) {
        _answers.value = answersList
    }

    fun updateAnswer(questionNumber: Int, answer: Int) {
        val currentAnswers = _answers.value?.toMutableList() ?: mutableListOf()
        if (currentAnswers.size > questionNumber && questionNumber >= 0) {
            currentAnswers[questionNumber] = answer
            _answers.value = currentAnswers
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}