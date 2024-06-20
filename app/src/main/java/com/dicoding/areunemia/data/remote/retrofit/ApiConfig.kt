package com.dicoding.areunemia.data.remote.retrofit

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.dicoding.areunemia.BuildConfig
import com.dicoding.areunemia.R
import com.dicoding.areunemia.di.Injection
import com.dicoding.areunemia.utils.showLogoutAlertDialog
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object{
        private const val TIMEOUT = 120L
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
                    handleTokenExpiry(context)
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
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
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
                    handleTokenExpiry(context)
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
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
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
            Handler(Looper.getMainLooper()).post {
                showLogoutAlertDialog(context)
            }
        }

    }
}