package com.dicoding.areunemia.data.remote.retrofit

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.dicoding.areunemia.BuildConfig
import com.dicoding.areunemia.R
import com.dicoding.areunemia.di.Injection
import com.dicoding.areunemia.utils.showLogoutAlertDialog
import com.dicoding.areunemia.view.login.LoginActivity
import com.dicoding.areunemia.view.main.MainActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object{
        fun getApiService(token: String, context: Context): ApiService {
            val loggingInterceptor =
                if(BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = req.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                val response = chain.proceed(requestHeaders)
                if (response.code == 403) {
                    // Token expired, handle token expiration by logging out the user
                    handleTokenExpiry(context)
                    // Return a response indicating the user has been logged out
                    return@Interceptor response.newBuilder()
                        .code(403)
                        .message(context.getString(R.string.token_expired))
                        .build()
                } else {
                    response
                }
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

        fun getApiServiceML(token: String, context: Context): ApiService {
            val loggingInterceptor =
                if(BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = req.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                val response = chain.proceed(requestHeaders)
                if (response.code == 403) {
                    // Token expired, handle token expiration by logging out the user
                    handleTokenExpiry(context)
                    // Return a response indicating the user has been logged out
                    return@Interceptor response.newBuilder()
                        .code(403)
                        .message(context.getString(R.string.token_expired))
                        .build()
                } else {
                    response
                }
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_ML)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }

        private fun handleTokenExpiry(context: Context) {
            val userRepository = Injection.provideRepository(context)
            runBlocking {
                userRepository.logout()
            }
            // Show logout alert dialog on the main thread
            Handler(Looper.getMainLooper()).post {
                showLogoutAlertDialog(context)
            }
        }

        fun getApiServiceMock(): ApiService {
            val loggingInterceptor =
                if(BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://b524c09c-af98-434c-bae0-c1603421850b.mock.pstmn.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}