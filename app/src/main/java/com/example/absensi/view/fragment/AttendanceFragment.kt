package com.example.absensi.view.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.example.absensi.R
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.fragment_attendance.*

/**
 * A simple [Fragment] subclass.
 */
class AttendanceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }


}
