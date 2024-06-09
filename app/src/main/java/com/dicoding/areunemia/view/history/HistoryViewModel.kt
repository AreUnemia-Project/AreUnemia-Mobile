package com.dicoding.areunemia.view.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.remote.response.HistoryItem
import com.dicoding.areunemia.data.remote.response.HistoryResponse
import com.dicoding.areunemia.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel(private val repository: UserRepository) : ViewModel() {

    private val _listHistory = MutableLiveData<List<HistoryItem?>?>()
    val listHistory: LiveData<List<HistoryItem?>?> = _listHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getListHistories() {
        _isLoading.value = true
        val client = ApiConfig.getApiServiceMock().getListHistory()

        client.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                _isLoading.value = false
                response.body()?.let { historyResponse ->
                    if (historyResponse.status.equals("error")) {
                        _error.value = "error"
                    } else {
                        _listHistory.value = response.body()!!.data
                    }
                } ?: run {
                    _error.value = "error"
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object {
        private const val TAG = "HistoryViewModel"
    }

}