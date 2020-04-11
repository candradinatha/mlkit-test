package com.example.absensi.model.auth

import com.example.absensi.model.common.BaseResultData
import com.google.gson.annotations.SerializedName

class AuthResponse: BaseResultData() {

	@field:SerializedName("data")
	val data: AuthData? = null

}