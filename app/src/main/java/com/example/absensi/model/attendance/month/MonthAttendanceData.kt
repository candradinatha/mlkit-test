package com.example.absensi.model.attendance.month

import com.google.gson.annotations.SerializedName

data class MonthAttendanceData(

	@field:SerializedName("late")
	val late: Int? = null,

	@field:SerializedName("absent")
	val absent: Int? = null,

	@field:SerializedName("present")
	val present: Int? = null,

	@field:SerializedName("start_working_hour")
	val startWorkingHour: String? = null,

	@field:SerializedName("end_working_hour")
	val endWorkingHour: String? = null,

	@field:SerializedName("attendances")
	val attendances: List<MonthAttendanceItem>? = null

)