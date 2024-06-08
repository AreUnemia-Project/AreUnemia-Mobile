package com.dicoding.areunemia.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.di.Injection
import com.dicoding.areunemia.view.history.HistoryDetailViewModel
import com.dicoding.areunemia.view.history.HistoryViewModel
import com.dicoding.areunemia.view.login.LoginViewModel
import com.dicoding.areunemia.view.main.MainViewModel
import com.dicoding.areunemia.view.register.RegisterViewModel
import com.dicoding.areunemia.view.scan.ScanProcessViewModel
import com.dicoding.areunemia.view.scan.ScanViewModel

class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HistoryDetailViewModel::class.java) -> {
                HistoryDetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ScanProcessViewModel::class.java) -> {
                ScanProcessViewModel(repository) as T
            }
            // add view models here
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(context: Context) = ViewModelFactory(Injection.provideRepository(context))

    }
}