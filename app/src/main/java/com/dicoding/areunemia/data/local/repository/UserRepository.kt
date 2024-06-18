package com.dicoding.areunemia.data.local.repository

import com.dicoding.areunemia.data.local.pref.UserModel
import com.dicoding.areunemia.data.local.pref.UserPreference
import com.dicoding.areunemia.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    val apiService: ApiService, val apiServiceML: ApiService, private val userPreference: UserPreference
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        fun getInstance(apiService: ApiService,
                        apiServiceML: ApiService,
                        userPreference: UserPreference) = UserRepository(apiService, apiServiceML, userPreference)
    }
}