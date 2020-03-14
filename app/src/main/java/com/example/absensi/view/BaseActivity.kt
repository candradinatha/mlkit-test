package com.example.absensi.view

import android.annotation.SuppressLint
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.absensi.R
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


    fun setToolbarHome(title: String? = null, isPrimary: Boolean) {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayShowHomeEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
            if (isPrimary) {
                toolbar.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorPrimary))
                toolbar_title.setTextColor(ContextCompat.getColor(this@BaseActivity, R.color.colorOnPrimary))
            }
            else {
                toolbar.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorOnPrimary))
                toolbar_title.setTextColor(ContextCompat.getColor(this@BaseActivity, R.color.onBackground))
            }
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