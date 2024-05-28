package com.dicoding.areunemia.di

import android.content.Context
import com.dicoding.areunemia.data.local.repository.UserRepository
import com.dicoding.areunemia.data.local.pref.UserPreference
import com.dicoding.areunemia.data.local.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}