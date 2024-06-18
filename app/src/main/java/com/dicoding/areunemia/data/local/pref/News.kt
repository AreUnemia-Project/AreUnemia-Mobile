package com.dicoding.areunemia.data.local.pref

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class News(
    val title: String,
    val description: String,
    val img: Int
) : Parcelable
