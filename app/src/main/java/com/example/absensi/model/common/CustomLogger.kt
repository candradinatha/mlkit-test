package com.example.absensi.model.common

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor

class CustomLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        val logName = "Attendance"
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val prettyPrintJson = GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create().toJson(
                    JsonParser().parse(message))
                largeLog(logName, prettyPrintJson)
            } catch (m: JsonSyntaxException) {
                Log.d(logName, message)
            }
        } else {
            Log.d(logName, message)
            return
        }
    }

    fun largeLog(tag: String, content: String){
        if (content.length > 4000) {
            Log.d(tag, content.substring(0, 4000))
            largeLog(tag, content.substring(4000))
        }
        else {
            Log.d(tag, content)
        }
    }
}
