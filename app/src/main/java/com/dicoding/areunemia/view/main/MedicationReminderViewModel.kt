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
import com.dicoding.areunemia.data.remote.response.MedicationResponse
import com.dicoding.areunemia.data.remote.response.MedicationsItem
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicationReminderViewModel (private val repository: UserRepository) : ViewModel() {

    private val _listMedication = MutableLiveData<List<MedicationsItem>?>()
    val listMedication: LiveData<List<MedicationsItem>?> = _listMedication

    private val _deleteMedicationResult = MutableLiveData<BaseResponse>()
    val deleteMedicationResult: LiveData<BaseResponse> = _deleteMedicationResult

    private val _doneMedicationResult = MutableLiveData<BaseResponse>()
    val doneMedicationResult: LiveData<BaseResponse> = _doneMedicationResult

    private val _skipMedicationResult = MutableLiveData<BaseResponse>()
    val skipMedicationResult: LiveData<BaseResponse> = _skipMedicationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getListMedications(context: Context) {
        _isLoading.value = true
        val client = repository.apiService.getListMedication()

        client.enqueue(object : Callback<MedicationResponse> {
            override fun onResponse(
                call: Call<MedicationResponse>,
                response: Response<MedicationResponse>
            ) {
                _isLoading.value = false
                response.body()?.let { medicResponse ->
                    if (medicResponse.status == "error") {
                        _error.value = context.getString(R.string.error_message)
                    } else {
                        _listMedication.value = response.body()!!.medications
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

            override fun onFailure(call: Call<MedicationResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun deleteMedication(medicationName: String, context: Context) {
        _isLoading.value = true
        val client = repository.apiService.deleteMedicationFromName(medicationName)

        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                _isLoading.value = false
                response.body()?.let { medicResponse ->
                    if (medicResponse.status == "error") {
                        _error.value = context.getString(R.string.error_message)
                    } else {
                        _deleteMedicationResult.value = medicResponse
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
            }
        })
    }

    fun updateMedication(medicationName: String, schedule: String, updateType: String, context: Context) {
        _isLoading.value = true
        val client = repository.apiService.updateMedicationSpecific(medicationName, schedule)

        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                _isLoading.value = false
                response.body()?.let { medicResponse ->
                    if (medicResponse.status == "error") {
                        _error.value = context.getString(R.string.error_message)
                    } else {
                        if (updateType == "Skip") {
                            _skipMedicationResult.value = medicResponse
                        } else if (updateType == "Done") {
                            _doneMedicationResult.value = medicResponse
                        }
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
            }
        })
    }

    companion object {
        private const val TAG = "MedicationReminderViewModel"
    }
    
}