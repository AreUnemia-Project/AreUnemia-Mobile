package com.dicoding.areunemia.view.account

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.remote.response.BaseResponse
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileViewModel(private val repository: UserRepository) : ViewModel() {
    private val _editUserDataResult = MutableLiveData<BaseResponse>()
    val editUserDataResult: LiveData<BaseResponse> = _editUserDataResult

    private val _editPasswordResult = MutableLiveData<BaseResponse>()
    val editPasswordResult: LiveData<BaseResponse> = _editPasswordResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun updateUserData(name: String, birthDate: String, gender: String, context: Context) {
        _isLoading.value = true
        val client = repository.apiService.updateUserData(name, birthDate, gender)

        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                _isLoading.value = false
                response.body()?.let { editResponse ->
                    if (editResponse.status == "error") {
                        _error.value =
                            editResponse.message ?: context.getString(R.string.error_message)
                    } else {
                        _editUserDataResult.value = editResponse
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

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _error.value = t.message.toString()
            }
        })
    }

    fun updatePassword(oldPassword: String, password: String, confirmPassword: String, context: Context) {
        _isLoading.value = true
        val client = repository.apiService.updatePassword(oldPassword, password, confirmPassword)

        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                _isLoading.value = false
                response.body()?.let { editResponse ->
                    if (editResponse.status == "error") {
                        _error.value =
                            editResponse.message ?: context.getString(R.string.error_message)
                    } else {
                        _editPasswordResult.value = editResponse
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

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                _error.value = t.message.toString()
            }
        })
    }

    companion object{
        private const val TAG = "EditProfileViewModel"
    }
}