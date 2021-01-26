package com.example.absensi.view.fragment


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
import com.example.absensi.model.auth.UserDataRealm
import com.example.absensi.presenter.AttendanceContract
import com.example.absensi.presenter.AttendancePresenter
import com.example.absensi.presenter.BaseContract
import com.example.absensi.view.BaseActivity
import com.example.absensi.view.activity.RecognitionActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
        toolbar_title.text = getString(R.string.home_toolbar_title)

        userData = getUserData()
        initLineChart(line_chart)
        btn_check_in_out.setOnClickListener {
            with(Intent(context, RecognitionActivity::class.java)) {
                this.putExtra(Constants.INTENT_ATTENDANCE_ID, attendanceId)
                this.putExtra(Constants.INTENT_ATTENDANCE_ACTION, attendanceAction)
                startActivityForResult(this, Constants.CHECK_IN_OUT_RESULT)
            }
//        startActivity(Intent(context, FaceDetectionActivity::class.java))
        }
        tv_user_name.text = userData?.name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CHECK_IN_OUT_RESULT) {
            val name = data?.getStringExtra(Constants.ARGS_INTENT_NAME) ?: ""
            val time = data?.getStringExtra(Constants.ARGS_INTENT_TIME) ?: ""
            val isCheckIn = data?.getBooleanExtra(Constants.ARGS_INTENT_IS_CHECK_IN, false)
            isCheckIn?.let {
                if (it)
                    (activity as BaseActivity).checkInSuccessDialog(name, time)
                else
                    (activity as BaseActivity).checkOutSuccessDialog(name, time)
            }
        }
    }
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

    override fun getThisMonthAttendance(response: MonthAttendanceResponse) {
        isLoading(false)
        if (response.data != null )
            setChartData(line_chart, response.data)
    }

    override fun checkInResponse(response: TodayAttendanceResponse) = Unit
    override fun checkOutResponse(response: TodayAttendanceResponse) = Unit


    // local function

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
                tv_check_in_out_time?.setVisibility(true)
                btn_check_in_out?.text = getString(R.string.check_out)
                tv_check_in_out_time?.text = getString(R.string.check_in_at, Utilities.changeDateFormat(it.checkInAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!))
                checkInTime = Utilities.stringToDate(it.checkInAt, Constants.API_DATE_FORMAT, context!!)!!.time
                handler.postDelayed(runnable, 1000)
            } else if (it?.checkOutAt != null) {
                attendanceAction = 99
                tv_check_in_out_time?.setVisibility(true)
                btn_check_in_out?.setVisibility(false)
                tv_check_in_out_time?.text = getString(R.string.check_out_at, Utilities.changeDateFormat(it.checkOutAt, Constants.API_DATE_FORMAT, Constants.HOUR_DATE_FORMAT, context!!))
                checkInTime = Utilities.stringToDate(it.checkInAt, Constants.API_DATE_FORMAT, context!!)!!.time
                val checkOutTime = Utilities.stringToDate(it.checkOutAt, Constants.API_DATE_FORMAT, context!!)!!.time
                val workHours = checkOutTime - checkInTime!!
                val hours = TimeUnit.MILLISECONDS.toHours(workHours)
                val minutes = TimeUnit.MILLISECONDS.toMinutes(workHours)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(workHours)
                todays_work_hours?.text = "${hours.toTwoDigits()}:${(minutes-hours*60).toTwoDigits()}:${(seconds-minutes*60).toTwoDigits()}"
            } else {
                attendanceAction = Constants.CHECK_IN
                tv_check_in_out_time?.setVisibility(false)
                todays_work_hours?.text = "00:00:00"
            }

            tv_today?.text = Utilities.changeDateFormat(it?.createdAt, Constants.API_DATE_FORMAT, Constants.HOME_DATE_FORMAT, context!!)
            tv_attendance_person?.text = "${allAttendance}/${allAbsent + allAttendance}"
            tv_attended_person?.text = if (allAbsent == 0) getString(R.string.all_attend) else getString(R.string.people_not_attendance, allAbsent)
            tv_months_attendance?.text = getString(R.string.my_month_attendance, month)
        }
    }

    private fun getTodayAttendance(){
        isLoading(true)
        presenter.getTodayAttendance()
        presenter.getThisMonthAttendance()
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            scroll_view_home?.setVisibility(false)
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
            scroll_view_home?.setVisibility(true)

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
        val startTimeDataSet = arrayListOf<Entry>()
        val endTimeDataSet = arrayListOf<Entry>()

        // chart details
        tv_months_present?.text = present.toString()
        tv_months_absent?.text = absent.toString()
        tv_months_late?.text = late.toString()


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
        chart_months_attendance?.invalidate()

        // line chart
        attendanceData.attendances?.forEachIndexed { index, item ->
            xAxisLabel.add("Day ${index+1}")

            var checkInHour = "0"
            var checkInMin = "0"
            var checkOutHour = "0"
            var checkOutMin = "0"

            val workingTimeFormat = "HH:mm:ss"
            var startHour = Utilities.changeDateFormat(attendanceData.startWorkingHour, workingTimeFormat, "HH", context!!)
            var startMin = Utilities.changeDateFormat(attendanceData.startWorkingHour, workingTimeFormat, "mm", context!!)
            var endHour = Utilities.changeDateFormat(attendanceData.endWorkingHour, workingTimeFormat, "HH", context!!)
            var endMin = Utilities.changeDateFormat(attendanceData.endWorkingHour, workingTimeFormat, "mm", context!!)
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
            startTimeDataSet.add(
                Entry(
                    index.toFloat(),
                    ("$startHour.${startMin}").toFloat(),
                    item
                )
            )
            endTimeDataSet.add(
                Entry(
                    index.toFloat(),
                    ("$endHour.${endMin}").toFloat(),
                    item
                )
            )
        }

        dataSetList.add(LineDataSet(checkInDataSet, "Check In"))
        dataSetList.add(LineDataSet(checkOutDataSet, "Check Out"))
//        dataSetList.add(LineDataSet(startTimeDataSet, "Start of Work Hour"))
//        dataSetList.add(LineDataSet(endTimeDataSet, "End of Work Hour"))
        view?.xAxis?.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
        dataSetList.forEachIndexed { index, it ->
            it.run {
                setDrawIcons(false)
                valueTextSize = 10f
                lineWidth = 2f
                circleRadius = 3f
                color =
                    if (index == 0)
                        ContextCompat.getColor(context!!, R.color.color_yellow_stamp)
                    else if (index == 2 || index ==3)
                        ContextCompat.getColor(context!!, R.color.alert_text_color)
                    else
                        ContextCompat.getColor(context!!, R.color.colorSecondaryVariant)
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
