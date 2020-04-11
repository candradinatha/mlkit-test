package com.example.absensi.model.attendance.today

import com.example.absensi.model.common.BaseResultData
import com.google.gson.annotations.SerializedName

class TodayAttendanceResponse: BaseResultData() {

	@field:SerializedName("data")
	val data: TodayAttendanceData? = null

}
