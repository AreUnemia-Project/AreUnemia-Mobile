package com.dicoding.areunemia.view.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.remote.response.LoginResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loginUser(email: String, password: String, context: Context) {
        _isLoading.value = true
        val client = repository.apiService.login(email, password)

        client.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                response.body()?.let { loginResponse ->
                    if (loginResponse.status == "error") {
                        _error.value =
                            loginResponse.message ?: context.getString(R.string.error_message)
                    } else {
                        _loginResult.value = loginResponse
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

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message.toString()
            }
        })
    }
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}