package com.example.absensi.presenter

import com.example.absensi.common.Constants
import com.example.absensi.model.attendance.today.TodayAttendanceResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface AttendanceContract {

    interface View {
        fun getTodayAttendance(response: TodayAttendanceResponse)
        fun checkInResponse(response: TodayAttendanceResponse)
        fun checkOutResponse(response: TodayAttendanceResponse)
    }

    interface Presenter {
        fun getTodayAttendance()
        fun getTodayInstantAttendance(label: String)
        fun checkIn(id: Int?)
        fun checkOut(id: Int?)
    }

    interface Handler {

        @GET("${Constants.API_ACTION_ATTENDANCE}/${Constants.API_ACTION_ATTENDANCE_TODAY}")
        fun getTodayAttendance(): Observable<TodayAttendanceResponse>

        @PATCH("${Constants.API_ACTION_ATTENDANCE}/${Constants.API_ACTION_ATTENDANCE_IN}/{id}")
        fun checkIn(
            @Path("id") id: Int?
        ): Observable<TodayAttendanceResponse>

        @PATCH("${Constants.API_ACTION_ATTENDANCE}/${Constants.API_ACTION_ATTENDANCE_OUT}/{id}")
        fun checkOut(
            @Path("id") id: Int?
        ): Observable<TodayAttendanceResponse>

        @GET("${Constants.API_ACTION_ATTENDANCE_INSTANT}/${Constants.API_ACTION_ATTENDANCE_TODAY}/{args}")
        fun getTodayInstantAttendance(
            @Path(value = "args", encoded = true) label: String
        ): Observable<TodayAttendanceResponse>
    }

}
