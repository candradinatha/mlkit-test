package com.example.absensi.presenter

interface BaseContract {
    interface View {
        fun showError(title: String, message: String?)
    }

    interface Presenter {
        fun showError(title: String, message: String?)
    }
}

open class BasePresenter(private val baseView: BaseContract.View) : BaseContract.Presenter {
    override fun showError(title: String, message: String?) = baseView.showError(title, message)
}
