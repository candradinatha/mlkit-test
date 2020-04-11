package com.example.absensi.presenter

import com.example.absensi.common.Constants
import com.example.absensi.model.auth.AuthResponse
import io.reactivex.Observable
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthContract {
    interface View {
        fun loginResponse(response: AuthResponse)
        fun registerResponse(response: AuthResponse)
    }
    interface Presenter {
        fun register(data: HashMap<String, Any?>)
        fun login(data: HashMap<String, Any?>)
    }
    interface Handler {
        @FormUrlEncoded
        @POST(Constants.API_ACTION_LOGIN)
        fun login(@FieldMap data: HashMap<String, Any?>): Observable<AuthResponse>

        @FormUrlEncoded
        @POST(Constants.API_ACTION_REGISTER)
        fun register(@FieldMap data: HashMap<String, Any?>): Observable<AuthResponse>
    }
}