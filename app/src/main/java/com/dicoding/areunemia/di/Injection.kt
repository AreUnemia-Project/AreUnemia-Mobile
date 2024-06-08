package com.dicoding.areunemia.di

import android.content.Context
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.local.pref.UserPreference
import com.dicoding.areunemia.data.local.pref.dataStore
import com.dicoding.areunemia.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}