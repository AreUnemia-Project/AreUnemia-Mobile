package com.dicoding.areunemia.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryDetailResponse(

	@field:SerializedName("data")
	val data: PredictionItem? = null,

	@field:SerializedName("status")
	val status: String? = null
)


