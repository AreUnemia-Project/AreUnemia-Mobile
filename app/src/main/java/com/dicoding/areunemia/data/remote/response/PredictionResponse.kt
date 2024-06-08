package com.dicoding.areunemia.data.remote.response

import com.google.gson.annotations.SerializedName

data class PredictionResponse(

	@field:SerializedName("data")
	val data: PredictionResult? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PredictionResult(

	@field:SerializedName("result")
	val result: String? = null

)
