package com.dicoding.areunemia.view.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.remote.response.HistoryDetailResponse
import com.dicoding.areunemia.data.remote.response.HistoryItem
import com.dicoding.areunemia.data.remote.response.PredictionItem
import com.dicoding.areunemia.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryDetailViewModel (private val repository: UserRepository) : ViewModel() {

    private val _historyDetail = MutableLiveData<PredictionItem>()
    val historyDetail: LiveData<PredictionItem> = _historyDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getHistoryDetail(id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiServiceMock().getHistoryDetail(id)

        client.enqueue(object : Callback<HistoryDetailResponse> {
            override fun onResponse(
                call: Call<HistoryDetailResponse>,
                response: Response<HistoryDetailResponse>
            ) {
                _isLoading.value = false
                response.body()?.let { historyDetailResponse ->
                    if (historyDetailResponse.status.equals("error")) {
                        _error.value = "error"
                    } else {
                        _historyDetail.value = historyDetailResponse.data!!
                    }
                } ?: run {
                    _error.value = "error"
                }
            }

            override fun onFailure(call: Call<HistoryDetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "HistoryDetailViewModel"
    }

}