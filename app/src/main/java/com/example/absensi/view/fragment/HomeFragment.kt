package com.example.absensi.view.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.example.absensi.R
import com.example.absensi.view.DetectionActivity
import com.example.absensi.view.activity.RecognitionActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val present = 20
        val absent = 8
        val late = 2

        btn_check_in_out.setOnClickListener {
            startActivity(Intent(context, DetectionActivity::class.java))
        }

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


}
