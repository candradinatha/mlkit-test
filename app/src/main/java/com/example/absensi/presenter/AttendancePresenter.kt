package com.example.absensi.presenter

import com.example.absensi.handler.AttendanceHandler
import com.example.absensi.handler.ErrorHandler
import com.example.absensi.model.attendance.month.MonthAttendanceResponse
import com.example.absensi.model.attendance.today.TodayAttendanceResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AttendancePresenter(val view: AttendanceContract.View, val baseView: BaseContract.View): AttendanceContract.Presenter, BaseContract.View {

    private val handler = AttendanceHandler()
    private val basePresenter = BasePresenter(this)

    override fun getTodayAttendance() {
        handler.getTodayAttendance()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ErrorHandler<TodayAttendanceResponse>(basePresenter){
                override fun onNext(t: TodayAttendanceResponse) {
                    view.getTodayAttendance(t)
                }
            })
    }

    override fun checkIn(id: Int?) {
        handler.checkIn(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ErrorHandler<TodayAttendanceResponse>(basePresenter) {
                override fun onNext(t: TodayAttendanceResponse) {
                    view.checkInResponse(t)
                }
            })
    }

    override fun checkOut(id: Int?) {
        handler.checkOut(id)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ErrorHandler<TodayAttendanceResponse>(basePresenter) {
                override fun onNext(t: TodayAttendanceResponse) {
                    view.checkOutResponse(t)
                }
            })
    }

    override fun getTodayInstantAttendance(label: String) {
        handler.getTodayInstantAttendance(label)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ErrorHandler<TodayAttendanceResponse>(basePresenter) {
                override fun onNext(t: TodayAttendanceResponse) {
                    view.getTodayAttendance(t)
                }
            })
    }

    override fun getThisMonthAttendance() {
        handler.getThisMonthAttendance()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ErrorHandler<MonthAttendanceResponse>(basePresenter) {
                override fun onNext(t: MonthAttendanceResponse) {
                    view.getThisMonthAttendance(t)
                }
            })
    }

    override fun showError(title: String, message: String?) {
        baseView.showError(title, message)
    }
}