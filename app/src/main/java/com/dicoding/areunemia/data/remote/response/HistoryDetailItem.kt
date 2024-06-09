package com.dicoding.areunemia.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryDetailItem(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("eyePhoto")
    val eyePhoto: String? = null,

    @field:SerializedName("predictionId")
    val predictionId: String? = null,

    @field:SerializedName("predictionResult")
    val predictionResult: String? = null,

    @field:SerializedName("questionnaireAnswers")
    val questionnaireAnswers: QuestionnaireAnswers

)