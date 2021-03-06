package com.example.absensi.view.fragment


import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.example.absensi.R
import com.example.absensi.common.Constants
import com.example.absensi.common.Utilities
import com.example.absensi.common.toTwoDigits
import com.example.absensi.model.attendance.today.TodayAttendanceData
import com.example.absensi.model.attendance.today.TodayAttendanceResponse
import com.example.absensi.presenter.AttendanceContract
import com.example.absensi.presenter.AttendancePresenter
import com.example.absensi.presenter.BaseContract
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.fragment_attendance.*
import kotlinx.android.synthetic.main.layout_toolbar_elevation_zero.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class AttendanceFragment : BaseFragment(), AttendanceContract.View, BaseContract.View {

    private var checkInTime: Long? = null
    private val handler  = Handler()
    private val presenter = AttendancePresenter(this, this)
    private val currentTimeRunnable: Runnable = object : Runnable {
        override fun run() {
            displayCurrentTimeChanges()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarHome(toolbar)
        toolbar_title.text = getText(R.string.attendance_toolbar_title)
        toolbar_title.setTextColor(ContextCompat.getColor(context!!, R.color.background))
        toolbar.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorPrimary))

        val chartData = arrayListOf<PieEntry>()
        chartData.add(PieEntry(8f, "Absent"))
        chartData.add(PieEntry(20f, "Present"))
        chartData.add(PieEntry(2f, "Late"))

        val chartColors = arrayListOf<Int>()
        chartColors.add(ContextCompat.getColor(context!!, R.color.alert_text_color))
        chartColors.add(ContextCompat.getColor(context!!, R.color.colorPrimary))
        chartColors.add(ContextCompat.getColor(context!!, R.color.color_yellow_stamp))

        val dataSet = PieDataSet(chartData, "")
        dataSet.colors = chartColors
        val pieData  = PieData(dataSet)

        chart_attendance.description.isEnabled = false
        chart_attendance.data = pieData

        handler.postDelayed(currentTimeRunnable, 1000)

    }

    override fun onResume() {
        super.onResume()
        getTodayAttendance()
    }

    override fun getTodayAttendance(response: TodayAttendanceResponse) {
        isLoading(false)
        updateTodayAttendance(response.data)
    }

    override fun checkInResponse(response: TodayAttendanceResponse) = Unit

    override fun checkOutResponse(response: TodayAttendanceResponse) = Unit

    override fun showError(title: String, message: String?) {
        isLoading(false)
        super.showError(title, message)
        showToast( message?: title)
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) Utilities.showProgressDialog(context!!) else Utilities.hideProgressDialog()
    }

    private fun updateTodayAttendance(data: TodayAttendanceData?) {
        data.let {

            if (it?.checkInAt != null && it.checkOutAt == null) {
                todays_check_in_time.text = Utilities.changeDateFormat(it.checkInAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!)
                checkInTime = Utilities.stringToDate(it.checkInAt, Constants.API_DATE_FORMAT, context!!)!!.time
            } else if (it?.checkOutAt != null) {
                todays_check_in_time.text = Utilities.changeDateFormat(it.checkInAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!)
                todays_check_out_time.text = Utilities.changeDateFormat(it.checkOutAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!)
                checkInTime = Utilities.stringToDate(it.checkInAt, Constants.API_DATE_FORMAT, context!!)!!.time
                val checkOutTime = Utilities.stringToDate(it.checkOutAt, Constants.API_DATE_FORMAT, context!!)!!.time
                val workHours = checkOutTime - checkInTime!!
                val hours = TimeUnit.MILLISECONDS.toHours(workHours)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(workHours)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(workHours)
                todays_work_hours.text = "${hours.toTwoDigits()}:${(minutes-hours*60).toTwoDigits()}:${(seconds-minutes*60).toTwoDigits()}"
            } else {
                todays_work_hours.text = "00:00:00"
            }

            tv_todays_date.text = Utilities.changeDateFormat(it?.createdAt, Constants.API_DATE_FORMAT, Constants.HOME_DATE_FORMAT, context!!)
        }
    }

    private fun displayCurrentTimeChanges() {
        val currentTime = Calendar.getInstance()
        val amPm = currentTime.get(Calendar.AM_PM)
        val hours = currentTime.get(Calendar.HOUR_OF_DAY)
        val minutes = currentTime.get(Calendar.MINUTE)
        val seconds = currentTime.get(Calendar.SECOND)
        if (amPm == Calendar.AM && tv_todays_time!=null)
            tv_todays_time.text = "${hours.toTwoDigits()}:${(minutes).toTwoDigits()}:${(seconds).toTwoDigits()} AM"
        else if (amPm == Calendar.PM && tv_todays_time!=null)
            tv_todays_time.text = "${hours.toTwoDigits()}:${(minutes).toTwoDigits()}:${(seconds).toTwoDigits()} PM"
    }

    private fun getTodayAttendance(){
        isLoading(true)
        presenter.getTodayAttendance()
    }
}
