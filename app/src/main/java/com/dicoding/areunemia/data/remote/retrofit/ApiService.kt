package com.dicoding.areunemia.data.remote.retrofit

import com.dicoding.areunemia.data.remote.response.HistoryDetailResponse
import com.dicoding.areunemia.data.remote.response.HistoryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("Predictions")
    fun getListHistory(
    ): Call<HistoryResponse>

    @GET("Predictions/{id}")
    fun getHistoryDetail(
        @Path("id") id: String
    ): Call<HistoryDetailResponse>

}