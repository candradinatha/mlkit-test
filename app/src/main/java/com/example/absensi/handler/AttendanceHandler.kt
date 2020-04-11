package com.example.absensi.handler

import com.example.absensi.model.attendance.today.TodayAttendanceResponse
import com.example.absensi.presenter.AttendanceContract
import io.reactivex.Observable

class AttendanceHandler: BaseHandler() {

    private val service = getClient().create(AttendanceContract.Handler::class.java)

    fun getTodayAttendance(): Observable<TodayAttendanceResponse> {
        return service.getTodayAttendance()
    }
}