package com.example.absensi.presenter

import com.example.absensi.handler.AuthHandler
import com.example.absensi.handler.ErrorHandler
import com.example.absensi.model.auth.AuthResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AuthPresenter(val view: AuthContract.View, val baseView: BaseContract.View): AuthContract.Presenter, BaseContract.View {

    val basePresenter = BasePresenter(this)
    val handler = AuthHandler()

    override fun register(data: HashMap<String, Any?>) {
        handler.register(data)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ErrorHandler<AuthResponse>(basePresenter){
                override fun onNext(t: AuthResponse) {
                    view.registerResponse(t)
                }
            })
    }

    override fun login(data: HashMap<String, Any?>) {
        handler.login(data)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ErrorHandler<AuthResponse>(basePresenter){
                override fun onNext(t: AuthResponse) {
                    view.loginResponse(t)
                }
            })
    }

    override fun showError(title: String, message: String?) {
        baseView.showError(title, message)
    }
}