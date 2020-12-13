package com.example.absensi.common

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.Window
import com.example.absensi.R
import com.example.absensi.handler.BaseHandler
import com.example.absensi.model.common.APIError
import io.realm.internal.android.JsonUtils.stringToDate
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utilities {

    fun getApiUrlForHttps() : String {
        return  String.format("https://%1\$s/%2\$s/", Constants.DEBUG_DOMAIN, Constants.SERVER_PATH)
    }

    fun parseError(response: Response<*>): APIError {
        val converter: Converter<ResponseBody, APIError> = BaseHandler().getClient()
            .responseBodyConverter(APIError::class.java, arrayOfNulls<Annotation>(0))

        val error: APIError

        try {
            error = converter.convert(response.errorBody()!!)
        } catch (e: IOException) {
            e.printStackTrace()
            return APIError()
        }
        return error
    }

    private lateinit var dialog: Dialog

    fun showProgressDialog(context: Context) {
        dialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_progress_bar, null)
        dialog.run {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCanceledOnTouchOutside(false)
            setContentView(view)
            show()
        }
    }

    fun hideProgressDialog() {
        if (this::dialog.isInitialized && dialog.isShowing) dialog.dismiss()
    }


    fun stringToDate(date: String?, dateFormat: String, context: Context) : Date? {
        try {
            val simpleDateFormat = SimpleDateFormat(dateFormat, currentLocale(context))
            return date?.let { simpleDateFormat.parse(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }


    fun changeDateFormat(date: String?, dateFormat: String, dateFormat2: String, context: Context) : String {
        val dateCalendar =
            stringToDate(
                date,
                dateFormat,
                context
            )
        try {
            val dateFormatter = SimpleDateFormat(dateFormat2,
                currentLocale(
                    context
                )
            )
            return dateFormatter.format(dateCalendar)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun currentLocale(context: Context): Locale {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            context.resources.configuration.locales.get(0)
//        } else {
//
//            context.resources.configuration.locale
//        }
        return Locale.ENGLISH
    }
}