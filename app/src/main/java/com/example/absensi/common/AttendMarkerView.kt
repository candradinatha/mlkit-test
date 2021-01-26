package com.example.absensi.common

import android.content.Context
import android.graphics.Canvas
import com.example.absensi.model.attendance.month.MonthAttendanceItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.layout_chart_marker.view.*

class AttendMarkerView(context: Context?, layoutResource: Int, val chart: LineChart?):
    MarkerView(context, layoutResource){

    private val dateFormat = "EEE, dd MMMM yyyy"
    private val timeFormat = "HH:mm"

    override fun refreshContent(e: Entry, highlight: Highlight?) {
        chartView = chart
        val data = e.data as MonthAttendanceItem
        tv_date?.text = Utilities.changeDateFormat(data.createdAt, Constants.API_DATE_FORMAT, dateFormat, context)
        check_in?.text =
            if (data.checkInAt == null && data.checkOutAt == null)
                "ABSENT"
            else
                Utilities.changeDateFormat(data.checkInAt, Constants.API_DATE_FORMAT, timeFormat, context)
        check_out?.text =
            if (data.checkInAt == null && data.checkOutAt == null)
                "ABSENT"
            else
                Utilities.changeDateFormat(data.checkOutAt, Constants.API_DATE_FORMAT, timeFormat, context)

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

    override fun draw(canvas: Canvas?, posX: Float, posY: Float) {
        super.draw(canvas, posX, posY)
        getOffsetForDrawingAtPoint(posX, posY)
    }
}