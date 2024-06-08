package com.dicoding.areunemia.view.scan

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.remote.response.PredictionResponse
import com.dicoding.areunemia.data.remote.response.QuestionnaireAnswers
import com.dicoding.areunemia.data.remote.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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

    fun uploadScan(eyePhoto: MultipartBody.Part, questionnaireAnswers: RequestBody) {
        _isLoading.value = true
        val client = ApiConfig.getApiServiceMock().postScan(eyePhoto, questionnaireAnswers)

        client.enqueue(object : Callback<PredictionResponse> {
            override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    response.body()?.let { uploadResponse ->
                        if (uploadResponse.status == "error") {
                            _error.value = "Error"
                        } else {
                            _uploadResult.value = uploadResponse
                        }
                    } ?: run {
                        _error.value = "Error"
                    }
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorResponse ?: "").getString("message")
                    } catch (e: Exception) {
                        response.message()
                    }
                    _error.value = errorMessage
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message.toString()
            }
        })

    }

}