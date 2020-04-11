package com.example.absensi.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.absensi.R
import com.example.absensi.common.*
import com.example.absensi.model.auth.AuthResponse
import com.example.absensi.model.auth.UserData
import com.example.absensi.model.auth.UserDataRealm
import com.example.absensi.presenter.AuthContract
import com.example.absensi.presenter.AuthPresenter
import com.example.absensi.view.BaseActivity
import com.example.absensi.view.MainActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), AuthContract.View {

    private val presenter = AuthPresenter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initInputTextGroup()
        btn_login.setOnClickListener { login() }
        btn_register.setOnClickListener { startActivity(Intent(Intent(this, RegisterActivity::class.java))) }
    }

    override fun loginResponse(response: AuthResponse) {
        isLoading(false)
        intentLoginSuccess(response)
    }

    override fun registerResponse(response: AuthResponse) {
    }

    override fun showError(title: String, message: String?) {
        isLoading(false)
        super.showError(title, message)
        if (message!=null)
            snackBarActionClose(message, btn_login)
        else
            snackBarActionClose(title, btn_login)
    }

    private fun initInputTextGroup() {
        inputTextGroup[til_credential.editText!!] = til_credential
        inputTextGroup[til_password.editText!!] = til_password
        inputTextGroup.forEach {
            it.key.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    it.value.isErrorEnabled = false
                    it.value.error = null
                }

            })
        }
    }

    private fun login() {
        if (isValid()) {
            val loginData = hashMapOf<String, Any?>()
            loginData[Constants.API_LOGIN_REQ_CREDENTIAL] = til_credential.editText?.text.toString()
            loginData[Constants.API_LOGIN_REQ_PASSWORD] = til_password.editText?.text.toString()
            isLoading(true)

            presenter.login(data = loginData)
        }
    }

    private fun isValid(): Boolean {
        val valid = arrayListOf<Boolean>()

        inputTextGroup.forEach {
            valid.add(Validation.isTextValid(Constants.VALIDATION_EMPTY, editText = it.key, textInputLayout = it.value))
        }

        return !valid.contains(false)
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading)
            Utilities.showProgressDialog(this)
        else
            Utilities.hideProgressDialog()
    }

    private fun intentLoginSuccess(response: AuthResponse) {
        val userDataRealm = UserDataRealm()
        val userData = response.data?.userData

        userDataRealm.let {
            it.email = userData?.email
            it.employeeId  = userData?.employeeId
            it.id = userData?.id
            it.name = userData?.name
            it.phone = userData?.phone
        }
        response.data?.accessToken?.let {  Preferences(this).accessToken = it }
        userDataRealm.save()
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}
