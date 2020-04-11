package com.example.absensi.common

import android.widget.AutoCompleteTextView
import android.widget.EditText
import com.example.absensi.R
import com.google.android.material.textfield.TextInputLayout

object Validation {
    fun isTextValid(vararg types: Int, editText: EditText, textInputLayout: TextInputLayout, minimum: Int = 0) : Boolean {

        for (type in types) {
            when (type) {
                Constants.VALIDATION_EMPTY -> {
                    if (editText.text!!.trim().isEmpty()) {
                        textInputLayout.error = null
                        textInputLayout.isErrorEnabled = true
                        textInputLayout.error = GlobalClass.applicationContext()!!.getString(R.string.error_cannot_be_empty, editText.hint.toString())
                        return false
                    }
                }

//                Constants.VALIDATION_PHONE -> {
//                    val pattern = Pattern.compile(Constant.REGEX_PHONE)
//                    if (!pattern.matcher(editText.text.toString().trim()).find()) {
//                        if (textInputLayout.error != null) {
//                            textInputLayout.error = null
//                        }
//                        textInputLayout.error = GlobalClass.applicationContext().getString(R.string.kolom_tidak_valid, editText.hint.toString())
//                        return false
//                    }
//                }

                Constants.VALIDATION_MINIMUM -> {
                    if (editText.text!!.toString().trim().count() < minimum) {
                        if (textInputLayout.error != null) {
                            textInputLayout.error = null
                        }
                        textInputLayout.isErrorEnabled = true
                        textInputLayout.error = GlobalClass.applicationContext()!!.getString(R.string.error_input_must_more_than, minimum)

                        return false
                    }
                }
            }
        }

        return true
    }

    fun isTextAutoCompleteValid(vararg types: Int, autoComplete: AutoCompleteTextView, textInputLayout: TextInputLayout, minimum: Int = 0): Boolean {

        for (type in types) {
            when (type) {
                Constants.VALIDATION_EMPTY -> {
                    if (autoComplete.text!!.trim().isEmpty()) {
                        textInputLayout.error = null
                        textInputLayout.isErrorEnabled = true
                        textInputLayout.error = GlobalClass.applicationContext()!!.getString(R.string.error_cannot_be_empty, autoComplete.hint.toString())
                        return false
                    }
                }

//                Constants.VALIDATION_PHONE -> {
//                    val pattern = Pattern.compile(Constant.REGEX_PHONE)
//                    if (!pattern.matcher(editText.text.toString().trim()).find()) {
//                        if (textInputLayout.error != null) {
//                            textInputLayout.error = null
//                        }
//                        textInputLayout.error = GlobalClass.applicationContext().getString(R.string.kolom_tidak_valid, editText.hint.toString())
//                        return false
//                    }
//                }

                Constants.VALIDATION_MINIMUM -> {
                    if (autoComplete.text!!.toString().trim().count() < minimum) {
                        if (textInputLayout.error != null) {
                            textInputLayout.error = null
                        }
                        textInputLayout.isErrorEnabled = true
                        textInputLayout.error = GlobalClass.applicationContext()!!.getString(R.string.error_input_must_more_than, minimum)

                        return false
                    }
                }
            }
        }

        return true

    }

}
