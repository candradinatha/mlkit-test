package com.example.absensi.common

object Constants {

    //ip5 ip address
    //const val DEBUG_DOMAIN = "172.20.10.3:8000"
    //oppo ip address
    const val DEBUG_DOMAIN = "192.168.43.133:8000"
    const val SERVER_PATH = "api"
    const val ACTIVITY_MAIN = "activity_main"

    const val MENU_HOME = 0
    const val MENU_ATTENDANCE = 1
    const val MENU_ACCOUNT = 2

    const val SHARED_PREFERENCES = "shared_preferences"

    //Validation Types
    const val VALIDATION_EMPTY = 0
    const val VALIDATION_PHONE = 1
    const val VALIDATION_NAME = 2
    const val VALIDATION_EMAIL = 3
    const val VALIDATION_MINIMUM = 4
    const val VALIDATION_ADDRESS = 5

    const val API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val HOME_DATE_FORMAT = "EEEE, dd MMMM yyyy"
    const val MONTH_DATE_FORMAT = "MMMM"
    const val HOUR_DATE_FORMAT = "HH:mm"

    // API
    //auth
    const val API_ACTION_LOGIN = "login"
    const val API_ACTION_REGISTER = "register"

    const val API_REGISTER_REQ_NAME = "name"
    const val API_REGISTER_REQ_EMAIL = "email"
    const val API_REGISTER_REQ_PHONE = "phone"
    const val API_REGISTER_REQ_EMPLOYEE_ID = "employee_id"
    const val API_REGISTER_REQ_PASSWORD = "password"

    const val API_LOGIN_REQ_CREDENTIAL = "credential"
    const val API_LOGIN_REQ_PASSWORD = "password"

    // attendance
    const val API_ACTION_ATTENDANCE = "attendance"
    const val API_ACTION_ATTENDANCE_TODAY = "today"
}