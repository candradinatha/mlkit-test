package com.example.absensi.handler

import com.example.absensi.model.attendance.month.MonthAttendanceResponse
import com.example.absensi.model.attendance.today.TodayAttendanceResponse
import com.example.absensi.presenter.AttendanceContract
import io.reactivex.Observable

class AttendanceHandler: BaseHandler() {

    private val service = getClient().create(AttendanceContract.Handler::class.java)

    fun getTodayAttendance(): Observable<TodayAttendanceResponse> {
        return service.getTodayAttendance()
    }

    fun checkIn(id: Int?): Observable<TodayAttendanceResponse> {
        return service.checkIn(id)
    }

    fun checkOut(id: Int?): Observable<TodayAttendanceResponse> {
        return service.checkOut(id)
    }

    fun getTodayInstantAttendance(label: String): Observable<TodayAttendanceResponse> {
        return service.getTodayInstantAttendance(label)
    }

    fun getThisMonthAttendance(): Observable<MonthAttendanceResponse> {
        return service.getThisMonthAttendance()
    }
}