package com.example.absensi.view.fragment


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.example.absensi.R
import com.example.absensi.common.*
import com.example.absensi.model.Chart
import com.example.absensi.model.attendance.month.MonthAttendanceData
import com.example.absensi.model.attendance.month.MonthAttendanceResponse
import com.example.absensi.model.attendance.today.TodayAttendanceData
import com.example.absensi.model.attendance.today.TodayAttendanceResponse
import com.example.absensi.presenter.AttendanceContract
import com.example.absensi.presenter.AttendancePresenter
import com.example.absensi.presenter.BaseContract
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.android.synthetic.main.fragment_attendance.*
import kotlinx.android.synthetic.main.fragment_attendance.line_chart
import kotlinx.android.synthetic.main.fragment_attendance.shimmer
import kotlinx.android.synthetic.main.fragment_attendance.todays_work_hours
import kotlinx.android.synthetic.main.fragment_attendance.tv_months_attendance
import kotlinx.android.synthetic.main.fragment_home.*
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

        initLineChart(line_chart)
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

    override fun getThisMonthAttendance(response: MonthAttendanceResponse) {
        isLoading(false)
        if (response.data != null )
            setChartData(line_chart, response.data)
    }

    override fun showError(title: String, message: String?) {
        isLoading(false)
        super.showError(title, message)
        showToast( message?: title)
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            layout_main_content?.setVisibility(false)
            shimmer?.run {
                setVisibility(true)
                showShimmer(true)
            }
        } else {
            shimmer?.run{
                stopShimmer()
                hideShimmer()
                setVisibility(false)
            }
            layout_main_content?.setVisibility(true)
        }
    }

    private fun updateTodayAttendance(data: TodayAttendanceData?) {
        data.let {

            val month = Utilities.changeDateFormat(it?.createdAt, Constants.API_DATE_FORMAT, Constants.MONTH_DATE_FORMAT, context!!)

            if (it?.checkInAt != null && it.checkOutAt == null) {
                todays_check_in_time?.text = Utilities.changeDateFormat(it.checkInAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!)
                checkInTime = Utilities.stringToDate(it.checkInAt, Constants.API_DATE_FORMAT, context!!)!!.time
            } else if (it?.checkOutAt != null) {
                todays_check_in_time?.text = Utilities.changeDateFormat(it.checkInAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!)
                todays_check_out_time?.text = Utilities.changeDateFormat(it.checkOutAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!)
                checkInTime = Utilities.stringToDate(it.checkInAt, Constants.API_DATE_FORMAT, context!!)!!.time
                val checkOutTime = Utilities.stringToDate(it.checkOutAt, Constants.API_DATE_FORMAT, context!!)!!.time
                val workHours = checkOutTime - checkInTime!!
                val hours = TimeUnit.MILLISECONDS.toHours(workHours)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(workHours)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(workHours)
                todays_work_hours?.text = "${hours.toTwoDigits()}:${(minutes-hours*60).toTwoDigits()}:${(seconds-minutes*60).toTwoDigits()}"
            } else {
                todays_work_hours?.text = "00:00:00"
            }

            tv_months_attendance?.text = getString(R.string.my_month_attendance, month)
            tv_todays_date?.text = Utilities.changeDateFormat(it?.createdAt, Constants.API_DATE_FORMAT, Constants.HOME_DATE_FORMAT, context!!)
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
        presenter.getThisMonthAttendance()
    }

    fun initLineChart(view: LineChart) {
        view.axisRight.isEnabled = false
        view.setBackgroundColor(Color.TRANSPARENT)
        view.setTouchEnabled(true)
        view.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {}
            override fun onNothingSelected() {}
        })

        val xAxis = view.xAxis
        val yAxis = view.axisLeft

        xAxis?.enableGridDashedLine(10f, 10f, 10f)
        yAxis?.run {
            enableGridDashedLine(10f, 10f, 10f)
            axisMaximum = 24f
            axisMinimum = 0f
        }

        // draw limit lines behind data instead of on top
        yAxis?.setDrawLimitLinesBehindData(true)
        xAxis?.setDrawLimitLinesBehindData(true)

        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // prevent duplicated axis label
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1f


        // rotate axis label
        xAxis.labelRotationAngle = -45f
        // add limit lines
//        yAxis?.addLimitLine(ll1);
//        yAxis?.addLimitLine(ll2);
        xAxis.textSize = 10f
        yAxis.textSize = 10f

        xAxis.mLabelHeight = ViewGroup.LayoutParams.WRAP_CONTENT

//        setChartData(view)
    }

    private fun setChartData(view: LineChart?, attendanceData: MonthAttendanceData) {
        val dataSetList = arrayListOf<LineDataSet>()
        val checkInDataSet = arrayListOf<Entry>()
        val checkOutDataSet = arrayListOf<Entry>()
        val xAxisLabel = arrayListOf<String>()
        val present = attendanceData.present ?: 0
        val absent = attendanceData.absent ?: 0
        val late = attendanceData.late ?: 0

        // pie chart config
        val chartData = arrayListOf<PieEntry>()
        chartData.add(PieEntry(absent.toFloat(), "Absent"))
        chartData.add(PieEntry(present.toFloat(), "Present"))
        chartData.add(PieEntry(late.toFloat(), "Late"))

        val chartColors = arrayListOf<Int>()
        chartColors.add(ContextCompat.getColor(context!!, R.color.alert_text_color))
        chartColors.add(ContextCompat.getColor(context!!, R.color.colorPrimary))
        chartColors.add(ContextCompat.getColor(context!!, R.color.color_yellow_stamp))

        val dataSet = PieDataSet(chartData, "")
        dataSet.colors = chartColors
        val pieData  = PieData(dataSet)

        chart_attendance.description.isEnabled = false
        chart_attendance.data = pieData
        chart_attendance?.invalidate()

        // line chart
        attendanceData.attendances?.forEachIndexed { index, item ->
            xAxisLabel.add("Day ${index+1}")

            var checkInHour = "0"
            var checkInMin = "0"
            var checkOutHour = "0"
            var checkOutMin = "0"
            if (item.checkInAt != null) {
                checkInHour = Utilities.changeDateFormat(item.checkInAt, Constants.API_DATE_FORMAT, "HH", context!!)
                checkInMin = Utilities.changeDateFormat(item.checkInAt, Constants.API_DATE_FORMAT, "mm", context!!)
            }
            if (item.checkOutAt != null) {
                checkOutHour = Utilities.changeDateFormat(item.checkOutAt, Constants.API_DATE_FORMAT, "HH", context!!)
                checkOutMin = Utilities.changeDateFormat(item.checkOutAt, Constants.API_DATE_FORMAT, "mm", context!!)
            }
            checkInDataSet.add(
                Entry(
                    index.toFloat(),
                    ("$checkInHour.${checkInMin}").toFloat(),
                    item
                )
            )
            checkOutDataSet.add(
                Entry(
                    index.toFloat(),
                    ("$checkOutHour.${checkOutMin}").toFloat(),
                    item
                )
            )
        }

        dataSetList.add(LineDataSet(checkInDataSet, "Check In"))
        dataSetList.add(LineDataSet(checkOutDataSet, "Check Out"))
        view?.xAxis?.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
        dataSetList.forEachIndexed { index, it ->
            it.run {
                setDrawIcons(false)
                valueTextSize = 10f
                lineWidth = 2f
                circleRadius = 3f
                color = if (index == 0) ContextCompat.getColor(context!!, R.color.color_yellow_stamp) else ContextCompat.getColor(context!!, R.color.colorSecondaryVariant)
                circleColors = arrayListOf(R.color.colorPrimaryVariant)
                setDrawCircleHole(false)
            }
        }
        // prevent legend from being cut off
        view?.legend?.isWordWrapEnabled = true

        val lineData = LineData(dataSetList as List<LineDataSet>)
        view?.data = lineData
        val mv = AttendMarkerView(context, R.layout.layout_chart_marker, view)
        view?.marker = mv
        view?.invalidate()

    }
}
