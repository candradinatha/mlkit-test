package com.example.absensi.common

object Constants {

    //ip5 ip address
    //const val DEBUG_DOMAIN = "172.20.10.3:8000"

    //oppo ip address
//    const val DEBUG_DOMAIN = "192.168.43.133:8000"

    // timedoor ip address
    // const val DEBUG_DOMAIN = "192.168.100:8000"

    //oppo ip address macbook
    const val DEBUG_DOMAIN = "006a8d708f47.ngrok.io"

    const val SERVER_PATH = "api"
    const val ACTIVITY_MAIN = "activity_main"

    const val MENU_HOME = 0
    const val MENU_ATTENDANCE = 1
    const val MENU_ACCOUNT = 2

    const val SHARED_PREFERENCES = "shared_preferences"

    // args
    const val CHECK_IN = 0
    const val CHECK_OUT = 1
    const val CHECK_IN_OUT_RESULT = 10
    const val IS_INSTANT = "IS_INSTANT"
    const val ARGS_INTENT_NAME = "name"
    const val ARGS_INTENT_TIME = "time"
    const val ARGS_INTENT_IS_CHECK_IN = "is_check_in"

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

    //intent vars
    const val INTENT_ATTENDANCE_ID = "attendance_id"
    const val INTENT_ATTENDANCE_ACTION = "attendance_action"

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
    const val API_ACTION_ATTENDANCE_INSTANT = "attendance-instant"
    const val API_ACTION_ATTENDANCE_TODAY = "today"
    const val API_ACTION_ATTENDANCE_IN = "check-in"
    const val API_ACTION_ATTENDANCE_OUT = "check-out"

    // models
    const val API_ACTION_MODEL = "models"
    const val API_ACTION_TRAIN = "train"
    const val API_ACTION_TRAIN_MODEL = "train-model"
    const val API_ACTION_LABEL = "label"
    const val API_ACTION_DOWNLOAD = "download"
    const val API_ACTION_VERSION = "version"
    const val API_ACTION_CHECK_VERSION = "check-version"

    const val FILE_NAME_SVM_TRAIN = "svm_train"
    const val FILE_NAME_SVM_TRAIN_MODEL = "svm_train_model"
    const val FILE_NAME_SVM_TRAIN_LABEL = "labelMap_train"
    const val FILE_NAME_VERSION = "version.json"

}