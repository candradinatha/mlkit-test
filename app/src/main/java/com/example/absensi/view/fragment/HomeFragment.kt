package com.example.absensi.view.fragment


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.absensi.R
import com.example.absensi.common.*
import com.example.absensi.model.Chart
import com.example.absensi.model.attendance.today.TodayAttendanceData
import com.example.absensi.model.attendance.today.TodayAttendanceResponse
import com.example.absensi.model.auth.UserDataRealm
import com.example.absensi.presenter.AttendanceContract
import com.example.absensi.presenter.AttendancePresenter
import com.example.absensi.presenter.BaseContract
import com.example.absensi.view.BaseActivity
import com.example.absensi.view.DetectionActivity
import com.example.absensi.view.FaceDetectionActivity
import com.example.absensi.view.activity.RecognitionActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.util.*
import java.util.concurrent.TimeUnit


class HomeFragment : BaseFragment(), AttendanceContract.View, BaseContract.View {

    private val realm = Realm.getDefaultInstance()
    private var userData: UserDataRealm? = null
    private val handler  = Handler()
    private val presenter = AttendancePresenter(this, this)
    private var checkInTime: Long? = null
    private var attendanceAction = 0
    private var attendanceId = 0
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            displayWorkHourChanges()
            handler.postDelayed(this, 1000)
        }
    }

    private val currentTimeRunnable: Runnable = object : Runnable {
        override fun run() {
            displayCurrentTimeChanges()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarHome(toolbar)

        setToolbarHome(toolbar)
        toolbar_title.text = getString(R.string.home_toolbar_title)

        userData = getUserData()
        initChart()
        initLineChart(line_chart)
        btn_check_in_out.setOnClickListener {
            with(Intent(context, RecognitionActivity::class.java)) {
                this.putExtra(Constants.INTENT_ATTENDANCE_ID, attendanceId)
                this.putExtra(Constants.INTENT_ATTENDANCE_ACTION, attendanceAction)
                startActivity(this)
            }
//        startActivity(Intent(context, FaceDetectionActivity::class.java))
        }
        tv_user_name.text = userData?.name
    }

//    override fun onStart() {
//        super.onStart()
//        getTodayAttendance()
//    }

    override fun onResume() {
        super.onResume()
        val preferences = Preferences(GlobalClass.applicationContext()!!)

        if (preferences.afterCheckInSuccess) {
            preferences.afterCheckInSuccess = false
            (activity as BaseActivity).showCheckInSuccessDialog()
        }

        if (preferences.afterCheckOutSuccess) {
            preferences.afterCheckOutSuccess = false
            (activity as BaseActivity).showCheckOutSuccessDialog()
        }

        getTodayAttendance()
    }

    // Contract

    override fun getTodayAttendance(response: TodayAttendanceResponse) {
        isLoading(false)
        updateTodayAttendance(response.data)
    }

    override fun showError(title: String, message: String?) {
        isLoading(false)
        super.showError(title, message)
        showToast( message?: title)
    }

    override fun checkInResponse(response: TodayAttendanceResponse) = Unit
    override fun checkOutResponse(response: TodayAttendanceResponse) = Unit


    // local function

    private fun initChart() {
        val present = 8
        val absent = 0
        val late = 2
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

        chart_months_attendance.description.isEnabled = false
        chart_months_attendance.setDrawEntryLabels(false)
        chart_months_attendance.legend.isEnabled = false
        chart_months_attendance.data = pieData


        // chart details
        tv_months_present.text = present.toString()
        tv_months_absent.text = absent.toString()
        tv_months_late.text = late.toString()

    }

    private fun getUserData(): UserDataRealm? {
        return realm.where(UserDataRealm::class.java).findFirst()
    }

    private fun updateTodayAttendance(data: TodayAttendanceData?) {
        data.let {
            attendanceId = it?.id ?:0
            val allAttendance = it?.allAttendance ?:0
            val allAbsent = it?.allAbsent ?: 0
            val month = Utilities.changeDateFormat(it?.createdAt, Constants.API_DATE_FORMAT, Constants.MONTH_DATE_FORMAT, context!!)
            handler.postDelayed(currentTimeRunnable, 1000)

            if (it?.checkInAt != null && it.checkOutAt == null) {
                attendanceAction = Constants.CHECK_OUT
                tv_check_in_out_time.setVisibility(true)
                btn_check_in_out.text = getString(R.string.check_out)
                tv_check_in_out_time.text = getString(R.string.check_in_at, Utilities.changeDateFormat(it.checkInAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!))
                checkInTime = Utilities.stringToDate(it.checkInAt, Constants.API_DATE_FORMAT, context!!)!!.time
                handler.postDelayed(runnable, 1000)
            } else if (it?.checkOutAt != null) {
                attendanceAction = 99
                tv_check_in_out_time.setVisibility(true)
                btn_check_in_out.setVisibility(false)
                tv_check_in_out_time.text = getString(R.string.check_out_at, Utilities.changeDateFormat(it.checkOutAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!))
                checkInTime = Utilities.stringToDate(it.checkInAt, Constants.API_DATE_FORMAT, context!!)!!.time
                val checkOutTime = Utilities.stringToDate(it.checkOutAt, Constants.API_DATE_FORMAT, context!!)!!.time
                val workHours = checkOutTime - checkInTime!!
                val hours = TimeUnit.MILLISECONDS.toHours(workHours)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(workHours)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(workHours)
                todays_work_hours.text = "${hours.toTwoDigits()}:${(minutes-hours*60).toTwoDigits()}:${(seconds-minutes*60).toTwoDigits()}"
            } else {
                attendanceAction = Constants.CHECK_IN
                tv_check_in_out_time.setVisibility(false)
                todays_work_hours.text = "00:00:00"
            }

            tv_today.text = Utilities.changeDateFormat(it?.createdAt, Constants.API_DATE_FORMAT, Constants.HOME_DATE_FORMAT, context!!)
            tv_attendance_person.text = "${allAttendance}/${allAbsent + allAttendance}"
            tv_attended_person.text = if (allAbsent == 0) getString(R.string.all_attend) else getString(R.string.people_not_attendance, allAbsent)
            tv_months_attendance.text = getString(R.string.my_month_attendance, month)
        }
    }

    private fun getTodayAttendance(){
        isLoading(true)
        presenter.getTodayAttendance()
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            shimmer?.run {
                startShimmer()
            }
        } else {
            shimmer?.run{
                stopShimmer()
                hideShimmer()
            }
        }
    }

    private fun displayWorkHourChanges() {
        val currentTime = Calendar.getInstance().timeInMillis
        if (checkInTime!=null) {
            val workHours = currentTime - checkInTime!!
            val hours = TimeUnit.MILLISECONDS.toHours(workHours)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(workHours)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(workHours)
            if (todays_work_hours!=null)
                todays_work_hours.text = "${hours.toTwoDigits()}:${(minutes-hours*60).toTwoDigits()}:${(seconds-minutes*60).toTwoDigits()}"
        }
    }

    private fun displayCurrentTimeChanges() {
        val currentTime = Calendar.getInstance()
        val amPm = currentTime.get(Calendar.AM_PM)
        val hours = currentTime.get(Calendar.HOUR_OF_DAY)
        val minutes = currentTime.get(Calendar.MINUTE)
        val seconds = currentTime.get(Calendar.SECOND)
        if (amPm == Calendar.AM && current_time!=null)
            current_time.text = "${hours.toTwoDigits()}:${(minutes).toTwoDigits()}:${(seconds).toTwoDigits()} AM"
        else if (amPm == Calendar.PM && current_time!=null)
            current_time.text = "${hours.toTwoDigits()}:${(minutes).toTwoDigits()}:${(seconds).toTwoDigits()} PM"
    }

    fun initLineChart(view: LineChart) {
        view.axisRight.isEnabled = false
        view.setBackgroundColor(Color.TRANSPARENT)
        view.legend.isEnabled = false
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
            axisMaximum = 23f
            axisMinimum = 0f
        }

        // draw limit lines behind data instead of on top
        yAxis?.setDrawLimitLinesBehindData(true)
        xAxis?.setDrawLimitLinesBehindData(true)

        // add limit lines
//        yAxis?.addLimitLine(ll1);
//        yAxis?.addLimitLine(ll2);
        xAxis.textSize = 10f
        yAxis.textSize = 10f

        xAxis.mLabelHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        setChartData(view)
    }

    fun setChartData(view: LineChart) {
        val data = Chart(23)
        val values = arrayListOf<Entry>()
        data.list.forEachIndexed { index, it ->
            values.add(Entry(index.toFloat(), it.toFloat()))
        }

        val lineDataSet = LineDataSet(values, "Data")
        lineDataSet.run {
            setDrawIcons(false)
            color = resources.getColor(R.color.colorPrimary)
            circleColors = arrayListOf(R.color.colorPrimaryVariant)
            valueTextSize = 10f
            lineWidth = 1f
            circleRadius = 3f
            setDrawCircleHole(false)
        }

        val lineData = LineData(listOf(lineDataSet))

        view.data = lineData
    }
}
