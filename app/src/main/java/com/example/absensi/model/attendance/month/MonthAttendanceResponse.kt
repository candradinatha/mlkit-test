package com.example.absensi.model.attendance.month

import com.example.absensi.model.common.BaseResultData
import com.google.gson.annotations.SerializedName

class MonthAttendanceResponse: BaseResultData() {

	@field:SerializedName("data")
	val data: MonthAttendanceData? = null
}