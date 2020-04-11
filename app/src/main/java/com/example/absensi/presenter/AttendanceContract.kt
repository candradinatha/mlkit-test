package com.example.absensi.presenter

import com.example.absensi.common.Constants
import com.example.absensi.model.attendance.today.TodayAttendanceResponse
import io.reactivex.Observable
import retrofit2.http.GET

interface AttendanceContract {

    interface View {
        fun getTodayAttendance(response: TodayAttendanceResponse)
    }

    interface Presenter {
        fun getTodayAttendance()
    }

    interface Handler {

        @GET("${Constants.API_ACTION_ATTENDANCE}/${Constants.API_ACTION_ATTENDANCE_TODAY}")
        fun getTodayAttendance(): Observable<TodayAttendanceResponse>
    }

}
