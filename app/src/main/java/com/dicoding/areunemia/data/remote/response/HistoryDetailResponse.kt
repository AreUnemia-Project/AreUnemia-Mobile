package com.dicoding.areunemia.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryDetailResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Data(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("eyePhoto")
	val eyePhoto: String? = null,

	@field:SerializedName("predictionId")
	val predictionId: String? = null,

	@field:SerializedName("questionnaireAnswers")
	val questionnaireAnswers: QuestionnaireAnswers? = null
)

data class QuestionnaireAnswers(

	@field:SerializedName("question7")
	val question7: String? = null,

	@field:SerializedName("question6")
	val question6: String? = null,

	@field:SerializedName("question9")
	val question9: String? = null,

	@field:SerializedName("question8")
	val question8: String? = null,

	@field:SerializedName("question10")
	val question10: String? = null,

	@field:SerializedName("question3")
	val question3: String? = null,

	@field:SerializedName("question2")
	val question2: String? = null,

	@field:SerializedName("question5")
	val question5: String? = null,

	@field:SerializedName("question4")
	val question4: String? = null,

	@field:SerializedName("question1")
	val question1: String? = null
)
