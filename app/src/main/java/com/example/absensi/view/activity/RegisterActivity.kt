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
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.layout_toolbar_elevation_zero.*

class RegisterActivity : BaseActivity(), AuthContract.View {

    private val presenter = AuthPresenter(this, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setToolbar(toolbar)
        toolbar_title.text = getString(R.string.register)
        initInputTextGroup()
        btn_register.setOnClickListener {
            register()
        }
    }

    // contract view
    override fun registerResponse(response: AuthResponse) {
        isLoading(false)
        intentRegisterSuccess(response)
    }

    override fun showError(title: String, message: String?) {
        isLoading(false)
        super.showError(title, message)
        if (message!= null)
            snackBarActionClose(message, btn_register)
        else
            snackBarActionClose(title, btn_register)
    }

    override fun loginResponse(response: AuthResponse) {
    }


    // function

    private fun initInputTextGroup() {
        inputTextGroup[til_email.editText!!] = til_email
        inputTextGroup[til_employee_id.editText!!] = til_employee_id
        inputTextGroup[til_name.editText!!] = til_name
        inputTextGroup[til_password.editText!!] = til_password
        inputTextGroup[til_password_repeat.editText!!] = til_password_repeat
        inputTextGroup[til_phone.editText!!] = til_phone

        inputTextGroup.forEach{
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
                    it.value.error = null
                    it.value.isErrorEnabled = false
                    if (it.key == til_phone.editText) {
                        if (it.key.text.toString() == "0"){
                            it.key.setText("")
                        }
                    }
                }

            })
        }
    }
    private fun register() {
        if (isValid()) {
            val registerData = hashMapOf<String, Any?>()
            registerData[Constants.API_REGISTER_REQ_NAME] = til_name.editText?.text.toString()
            registerData[Constants.API_REGISTER_REQ_EMAIL] = til_email.editText?.text.toString()
            registerData[Constants.API_REGISTER_REQ_EMPLOYEE_ID] = til_employee_id.editText?.text.toString()
            registerData[Constants.API_REGISTER_REQ_PHONE] = til_phone.editText?.text.toString()
            registerData[Constants.API_REGISTER_REQ_PASSWORD] = til_password.editText?.text.toString()

            isLoading(true)
            presenter.register(data = registerData)
        }
    }

    private fun isValid() : Boolean {
        val valid = arrayListOf<Boolean>()

        // empty validation
        inputTextGroup.forEach {
            valid.add(Validation.isTextValid(Constants.VALIDATION_EMPTY, editText = it.key, textInputLayout = it.value))
        }

        //re-pass validation
        if (til_password.editText?.text.toString() != til_password_repeat.editText?.text.toString()) {
            til_password_repeat.run {
                isErrorEnabled = true
                error = getString(R.string.error_password_not_match)
            }
            valid.add(false)
        }
        else
            valid.add(true)

        return !valid.contains(false)
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading)
            Utilities.showProgressDialog(this)
        else
            Utilities.hideProgressDialog()
    }

    private fun intentRegisterSuccess(response: AuthResponse) {
        val userDataRealm = UserDataRealm()
        val data = response.data?.userData
        userDataRealm.let {
            it.email = data?.email
            it.employeeId  = data?.employeeId
            it.id = data?.id
            it.name = data?.name
            it.phone = data?.phone
        }
        response.data?.accessToken?.let { Preferences(this).accessToken = it }
        userDataRealm.save()
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}
