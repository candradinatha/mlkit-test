package com.example.absensi.model.attendance.month

import com.google.gson.annotations.SerializedName

data class MonthAttendanceItem(

	@field:SerializedName("check_out_at")
	val checkOutAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("check_in_at")
	val checkInAt: String? = null
)