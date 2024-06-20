package com.dicoding.areunemia.data.remote.retrofit

import com.dicoding.areunemia.data.remote.response.BaseResponse
import com.dicoding.areunemia.data.remote.response.HistoryDetailResponse
import com.dicoding.areunemia.data.remote.response.HistoryResponse
import com.dicoding.areunemia.data.remote.response.LoginResponse
import com.dicoding.areunemia.data.remote.response.MedicationResponse
import com.dicoding.areunemia.data.remote.response.PredictionResponse
import com.dicoding.areunemia.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
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
    ): Call<BaseResponse>

    @FormUrlEncoded
    @PUT("api/updatePassword")
    fun updatePassword(
        @Field("oldPassword") oldPassword: String,
        @Field("password") password: String,
        @Field("confirmPassword") confirmPassword: String
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST("api/medication")
    fun addMedication(
        @Field("medicationName") medicationName: String,
        @Field("dosage") dosage: String,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String,
        @Field("schedule") schedule: String,
        @Field("notes") notes: String
    ): Call<BaseResponse>

    @GET("api/medication")
    fun getListMedication(
    ): Call<MedicationResponse>

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "api/medication", hasBody = true)
    fun deleteMedicationFromName(
        @Field("medicationName") medicationName: String
    ): Call<BaseResponse>

    @FormUrlEncoded
    @PUT("api/medication")
    fun updateMedicationSpecific(
        @Field("medicationName") medicationName: String,
        @Field("timeToRemove") timeToRemove: String
    ): Call<BaseResponse>

    @GET("api/predict/history")
    fun getListHistory(
    ): Call<HistoryResponse>

    @GET("api/predict/history/{id}")
    fun getHistoryDetail(
        @Path("id") id: String
    ): Call<HistoryDetailResponse>

    @Multipart
    @POST("api/predict")
    fun postScan(
        @Part eyePhoto: MultipartBody.Part,
        @Part("questionnaire_answers") answersBody: RequestBody
    ): Call<PredictionResponse>

}