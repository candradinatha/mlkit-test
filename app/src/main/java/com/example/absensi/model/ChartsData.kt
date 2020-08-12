package com.example.absensi.model

class Chart(val range: Int) {
    val count: Int = 10
    val list: List<Int>
        get() {
            val data = arrayListOf<Int>()
            for (i in 1..count) data += (Math.random().times(range)).toInt()
            return data
        }
}