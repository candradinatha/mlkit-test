package com.example.absensi.model.auth

import com.google.gson.annotations.SerializedName

data class AuthData(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("refresh_token")
	val refreshToken: String? = null,

	@field:SerializedName("user_data")
	val userData: UserData? = null
)