package com.dicoding.areunemia.view.scan

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

class ScanViewModel (private val repository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    companion object {
        private const val TAG = "ScanViewModel"
    }

}