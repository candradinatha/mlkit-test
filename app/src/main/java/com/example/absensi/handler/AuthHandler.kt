package com.example.absensi.handler

import com.example.absensi.model.auth.AuthResponse
import com.example.absensi.presenter.AuthContract
import io.reactivex.Observable

class AuthHandler: BaseHandler() {
    private val service = getClient().create(AuthContract.Handler::class.java)

    fun login(data: HashMap<String, Any?>): Observable<AuthResponse> {
        return service.login(data)
    }

    fun register(data: HashMap<String, Any?>): Observable<AuthResponse> {
        return service.register(data)
    }
}