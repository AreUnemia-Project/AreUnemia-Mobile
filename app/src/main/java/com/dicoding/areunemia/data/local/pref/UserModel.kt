package com.dicoding.areunemia.data.local.pref

data class UserModel(
    val name: String,
    val email: String,
    val birthDate: String,
    val gender: String,
    val token: String,
    val isLogin: Boolean = false
)