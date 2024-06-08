package com.dicoding.areunemia.view.register

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.remote.response.RegisterResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun registerUser(name: String, birthDate: String, gender: String, email: String, password: String, context: Context) {
        _isLoading.value = true
        val client = repository.apiService.register(name, birthDate, gender, email, password)

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                _isLoading.value = false
                response.body()?.let { registerResponse ->
                    if (registerResponse.status == "error") {
                        _error.value =
                            registerResponse.message ?: context.getString(R.string.error_message)
                    } else {
                        _registerResult.value = registerResponse
                    }
                }
                response.errorBody()?.let {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorResponse ?: "").getString("message")
                    } catch (e: Exception) {
                        response.message()
                    }
                    _error.value = errorMessage ?: context.getString(R.string.error_message)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _error.value = t.message.toString()
            }
        })
    }

    companion object{
        private const val TAG = "RegisterViewModel"
    }
}