package com.dicoding.areunemia.data.remote.response

import com.google.gson.annotations.SerializedName

data class EditPasswordResponse(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)
