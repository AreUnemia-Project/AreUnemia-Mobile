package com.dicoding.areunemia.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("data")
	val data: List<HistoryItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

