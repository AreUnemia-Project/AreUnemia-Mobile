package com.dicoding.areunemia.view.scan

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.remote.response.PredictionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanProcessViewModel(private val repository: UserRepository) : ViewModel() {
    private val _currentImageUri = MutableLiveData<Uri>()
    val currentImageUri: LiveData<Uri> = _currentImageUri

    private val _answers = MutableLiveData<List<Int>>()
    val answers: LiveData<List<Int>> = _answers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _uploadResult = MutableLiveData<PredictionResponse>()
    val uploadResult: LiveData<PredictionResponse> = _uploadResult

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

    fun uploadScan(eyePhoto: MultipartBody.Part, questionnaireAnswers: RequestBody, context: Context) {
        _isLoading.value = true
        val client = repository.apiServiceML.postScan(eyePhoto, questionnaireAnswers)

        client.enqueue(object : Callback<PredictionResponse> {
            override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                _isLoading.value = false
                response.body()?.let { uploadResponse ->
                        if (uploadResponse.status == "error") {
                            _error.value = context.getString(R.string.error_message)
                        } else {
                            _uploadResult.value = uploadResponse
                        }
                    }
                response.errorBody()?.let {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorResponse ?: "").getString("detail")
                    } catch (e: Exception) {
                        response.message()
                    }
                    _error.value = errorMessage ?: context.getString(R.string.error_message)
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message.toString()
            }
        })

    }

}