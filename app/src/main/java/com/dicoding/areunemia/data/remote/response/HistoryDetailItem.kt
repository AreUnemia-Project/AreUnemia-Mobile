package com.dicoding.areunemia.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryDetailItem(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("eyePhotoUrl")
    val eyePhoto: String? = null,

    @field:SerializedName("predictionId")
    val predictionId: String? = null,

    @field:SerializedName("result")
    val predictionResult: String? = null,

    @field:SerializedName("questionnaireAnswers")
    val questionnaireAnswers: QuestionnaireAnswers

)