package com.dicoding.areunemia.view.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.areunemia.R
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.remote.response.BaseResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMedicationViewModel (private val repository: UserRepository) : ViewModel() {
    private val _addMedicationResult = MutableLiveData<BaseResponse>()
    val addMedicationResult: LiveData<BaseResponse> = _addMedicationResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun addNewMedication(
        medicationName: String,
        dosage: String,
        startDate: String,
        endDate: String,
        schedule: String,
        notes: String,
        context: Context
    ) {
        _isLoading.value = true
        val client = repository.apiService.addMedication(medicationName, dosage, startDate, endDate, schedule, notes)

        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                _isLoading.value = false
                response.body()?.let { addResponse ->
                    if (addResponse.status == "error") {
                        _error.value =
                            addResponse.message ?: context.getString(R.string.error_message)
                    } else {
                        _addMedicationResult.value = addResponse
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

    companion object {
        private const val TAG = "AddMedicationViewModel"
    }
}