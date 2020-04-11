package com.example.absensi.view

import android.annotation.SuppressLint
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.absensi.R
import com.example.absensi.presenter.BaseContract
import com.google.android.material.textfield.TextInputLayout

@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity(), BaseContract.View {

    var inputTextGroup = hashMapOf<EditText, TextInputLayout>()
    var alertDialog: SweetAlertDialog? = null

    fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }


    fun setToolbarHome(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayShowHomeEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
        }
    }


    fun showAlertDialog(type: Int, title: String? = null, content: String? = null, confirmTitle: String? = null, cancelTitle: String? = null, confirmListener: ((SweetAlertDialog) -> Unit)? = null, cancelListener: ((SweetAlertDialog) -> Unit)? = null, alertImage: Int? = null) {
        alertDialog = SweetAlertDialog(this, type)

        if (title != null)
            alertDialog!!.titleText = title

        if (content != null)
            alertDialog!!.contentText = content

        if (confirmTitle != null) {
            alertDialog!!.confirmText = confirmTitle
            alertDialog!!.setConfirmClickListener(confirmListener)
        }

        if (cancelTitle != null) {
            alertDialog!!.cancelText = cancelTitle
            alertDialog!!.setCancelClickListener(cancelListener)
        }

        if (alertImage != null) {
            alertDialog!!.setCustomImage(alertImage)
        }

        alertDialog!!.show()

    }

    fun dismissAlertDialog() {
        alertDialog!!.dismissWithAnimation()
    }

    fun showLoadingBar(title: String, description: String? = null) {
        showAlertDialog(SweetAlertDialog.PROGRESS_TYPE, title , description )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showError(title: String, message: String?) {
    }
}