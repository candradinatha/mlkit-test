package com.example.absensi.view.fragment

import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.absensi.R
import com.example.absensi.common.Utilities
import com.example.absensi.presenter.BaseContract
import com.example.absensi.view.BaseActivity

open class BaseFragment : Fragment(), BaseContract.View {
    val TAG = this::class.java.simpleName

    override fun showError(title: String, message: String?) {
        Utilities.hideProgressDialog()
        message?.let { showToast(it) }
    }

    fun setToolbar(toolbar: Toolbar) {
        val baseActivity = activity as BaseActivity
        baseActivity.setSupportActionBar(toolbar)
        baseActivity.supportActionBar?.run {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    fun setToolbarHome(toolbar: Toolbar) {
        val baseActivity = activity as BaseActivity
        baseActivity.setSupportActionBar(toolbar)
        baseActivity.supportActionBar?.run {
            setDisplayShowHomeEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
        }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    open fun showLoading() {}
    open fun hideLoading() {}

}