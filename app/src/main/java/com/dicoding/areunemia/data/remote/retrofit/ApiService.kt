package com.dicoding.areunemia.data.remote.retrofit

import com.dicoding.areunemia.data.remote.response.EditPasswordResponse
import com.dicoding.areunemia.data.remote.response.EditUserDataResponse
import com.dicoding.areunemia.data.remote.response.HistoryDetailResponse
import com.dicoding.areunemia.data.remote.response.HistoryResponse
import com.dicoding.areunemia.data.remote.response.LoginResponse
import com.dicoding.areunemia.data.remote.response.PredictionResponse
import com.dicoding.areunemia.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("api/register")
    fun register(
        @Field("name") name: String,
        @Field("birthdate") birthDate: String,
        @Field("gender") gender: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("api/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @PUT("api/updateUser")
    fun updateUserData(
        @Field("name") name: String,
        @Field("birthdate") birthDate: String,
        @Field("gender") gender: String
    ): Call<EditUserDataResponse>

    @FormUrlEncoded
    @PUT("api/updatePassword")
    fun updatePassword(
        @Field("oldPassword") oldPassword: String,
        @Field("password") password: String,
        @Field("confirmPassword") confirmPassword: String
    ): Call<EditPasswordResponse>

    @GET("api/predictions")
    fun getListHistory(
    ): Call<HistoryResponse>

    @GET("api/predictions/{id}")
    fun getHistoryDetail(
        @Path("id") id: String
    ): Call<HistoryDetailResponse>

    @Multipart
    @POST("api/predictions")
    fun postScan(
        @Part eyePhoto: MultipartBody.Part,
        @Part("questionnaireAnswers") answersBody: RequestBody
    ): Call<PredictionResponse>

}