package com.example.absensi.handler

import android.util.Log
import com.example.absensi.R
import com.example.absensi.common.GlobalClass
import com.example.absensi.common.Utilities
import com.example.absensi.model.common.BaseResultData
import com.example.absensi.presenter.BaseContract
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.io.IOException
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class ErrorHandler <T : BaseResultData>(presenter: BaseContract.Presenter) : Observer<T> {
    private val TAG = "APIError Handler"
    override fun onComplete() {}
    override fun onSubscribe(d: Disposable) {}

    private val weakReference: WeakReference<BaseContract.Presenter> = WeakReference(presenter)

    override fun onError(e: Throwable) {
        val presenter = weakReference.get()
        when (e) {
            is HttpException -> {
                val error = Utilities.parseError(e.response())
                Log.i(TAG, e.response().code().toString())
                when (e.response().code()) {
                    404 -> error.error.errors.forEach { presenter?.showError(it.title, it.message) }
                    422 -> error.error.errors.forEach { presenter?.showError(it.title, it.message) }
                    401 -> error.error.errors.forEach { presenter?.showError(it.title, it.message) }
                    400 -> error.error.errors.forEach { presenter?.showError(it.title, it.message) }
                    else -> presenter?.showError(GlobalClass.applicationContext()!!.getString(R.string.error_internal_server), GlobalClass.applicationContext()!!.getString(R.string.error_internal_server))
                }
            }
            is UnknownHostException -> {
                Log.i(TAG, "No connection")
                presenter?.showError(GlobalClass.applicationContext()!!.getString(R.string.error_no_internet_connection), GlobalClass.applicationContext()!!.getString(R.string.error_no_internet_connection))
            }
            is SocketTimeoutException -> {
                Log.i(TAG, "Timeout == ${e.cause}")
                presenter?.showError(GlobalClass.applicationContext()!!.getString(R.string.error_request_timeout), GlobalClass.applicationContext()!!.getString(R.string.error_request_timeout))
            }
            is IOException -> {
                Log.i(TAG, "IO Exception == ${e.localizedMessage}")
                if (e.localizedMessage.toString().contains("Connection closed") || e.localizedMessage.toString().contains("Failed to connect"))
                    presenter?.showError(GlobalClass.applicationContext()!!.getString(R.string.error_no_internet_connection), GlobalClass.applicationContext()!!.getString(R.string.error_no_internet_connection))
                else
                    presenter?.showError(GlobalClass.applicationContext()!!.getString(R.string.error_internal_server), GlobalClass.applicationContext()!!.getString(R.string.error_internal_server))
            }
            else -> { Log.i(TAG, e.localizedMessage) }
        }
    }

}
