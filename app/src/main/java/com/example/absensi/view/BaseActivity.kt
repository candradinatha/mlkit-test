package com.example.absensi.view

import android.annotation.SuppressLint
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_toolbar.*

@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity() {


    fun setToolbar(title: String? = null) {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            title?.let {
                toolbar_title?.visibility = View.VISIBLE
                toolbar_title?.text = it
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}