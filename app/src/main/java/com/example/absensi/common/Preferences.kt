package com.example.absensi.common

import android.content.Context
import android.content.SharedPreferences

class Preferences(context: Context) {
    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor
    private val LOGIN_AS = "login_as"
    private val ACCESS_TOKEN = "access_token"
    private val FIRST_LAUNCH = "first_launch"
    private val USER_LOGGED_IN = "logged_in"
    private val USER_PHONE = "phone_number"
    private val TUTORIAL_LAUNCH = "first_tutorial_launch"
    private val BUSINESS_ID = "business_id"
    private val IS_FETCH_RECYCLER_LIST = "is_fetch_recycler_list"
    private val IS_ADDRESS_CHANGED = "is_address_changed"
    private val LAT = "latitude"
    private val LNG = "longitude"
    private val ADDRESS = "address"

    init {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun userLoggedOut() {
        editor.clear()
        userLoggedIn = false
    }

    // true -> owner, false -> manager
    var loginAs: Boolean
        set(value) = editor.putBoolean(LOGIN_AS, value).apply()
        get() = sharedPreferences.getBoolean(LOGIN_AS, false)

    var accessToken: String
        set(value) = editor.putString(ACCESS_TOKEN, value).apply()
        get() = sharedPreferences.getString(ACCESS_TOKEN, "") ?: ""

    var firstLaunch: Boolean
        set(value) = editor.putBoolean(FIRST_LAUNCH, value).apply()
        get() = sharedPreferences.getBoolean(FIRST_LAUNCH, true)

    var tutorialLaunch: Boolean
        set(value) = editor.putBoolean(TUTORIAL_LAUNCH, value).apply()
        get() = sharedPreferences.getBoolean(TUTORIAL_LAUNCH, true)

    var userLoggedIn: Boolean
        set(value) = editor.putBoolean(USER_LOGGED_IN, value).apply()
        get() = sharedPreferences.getBoolean(USER_LOGGED_IN, false)

    var phone: String
        set(value) = editor.putString(USER_PHONE, "0$value").apply()
        get() = sharedPreferences.getString(USER_PHONE, "") ?: ""

    var businessId: String
        set(value) = editor.putString(BUSINESS_ID, value).apply()
        get() = sharedPreferences.getString(BUSINESS_ID, "") ?: ""

    var isFetchRecyclerList: Boolean
        set(value) = editor.putBoolean(IS_FETCH_RECYCLER_LIST, value).apply()
        get() = sharedPreferences.getBoolean(IS_FETCH_RECYCLER_LIST, false)

    var isFetchDetailUpdates: Boolean
        set(value) = editor.putBoolean(IS_FETCH_RECYCLER_LIST, value).apply()
        get() = sharedPreferences.getBoolean(IS_FETCH_RECYCLER_LIST, false)

    // branch menu settings
    var isAddressChanged: Boolean
        set(value) = editor.putBoolean(IS_ADDRESS_CHANGED, value).apply()
        get() = sharedPreferences.getBoolean(IS_ADDRESS_CHANGED, false)

    var address: String
        set(value) = editor.putString(ADDRESS, value).apply()
        get() = sharedPreferences.getString(ADDRESS, "") ?: ""

    var latitude: String
        set(value) = editor.putString(LAT, value).apply()
        get() = sharedPreferences.getString(LAT, "") ?: ""

    var longitude: String
        set(value) = editor.putString(LNG, value).apply()
        get() = sharedPreferences.getString(LNG, "") ?: ""

}
