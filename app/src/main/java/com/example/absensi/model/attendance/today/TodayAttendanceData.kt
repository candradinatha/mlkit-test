package com.example.absensi.model.attendance.today

import com.example.absensi.model.auth.UserData
import com.google.gson.annotations.SerializedName

data class TodayAttendanceData(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("check_out_at")
	val checkOutAt: String? = null,

	@field:SerializedName("all_absent")
	val allAbsent: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("check_in_at")
	val checkInAt: String? = null,

	@field:SerializedName("all_attendance")
	val allAttendance: Int? = null,

	@field:SerializedName("user")
	val userData: UserData? = null
)