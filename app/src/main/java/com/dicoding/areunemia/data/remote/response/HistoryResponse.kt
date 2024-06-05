package com.dicoding.areunemia.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("predictionResult")
	val predictionResult: String? = null,

	@field:SerializedName("predictionId")
	val predictionId: String? = null
)
