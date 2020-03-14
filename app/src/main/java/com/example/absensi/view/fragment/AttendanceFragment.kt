package com.example.absensi.view.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.absensi.R
import com.github.mikephil.charting.data.*
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

        chartData.add(PieEntry(10f, 0f))
        chartData.add(PieEntry(20f, 1f))

        val dataSet = PieDataSet(chartData, "attendance label")

        val label = arrayListOf<String>()
        label.add("attendance")
        label.add("absent")

        val pieData  = PieData(dataSet)

        chart_attendance.data = pieData

    }


}
