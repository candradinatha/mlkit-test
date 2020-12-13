package com.example.absensi.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.absensi.BuildConfig
import com.example.absensi.R
import com.example.absensi.common.Preferences
import com.example.absensi.view.BaseActivity
import com.example.absensi.view.MainActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : BaseActivity(){
    private lateinit var preferences: Preferences
    private var firstLaunch: Boolean = true
    private val splashDuration: Long
        get() {
            return if (firstLaunch) {
                preferences.firstLaunch = false
                2000
            } else {
                1000
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        setVersion()

        preferences = Preferences(this)
        firstLaunch = preferences.firstLaunch

        scheduleSplashScreen()
//        getFireBaseToken()
    }

    private fun setVersion() {
        splash_version.text = "V ${BuildConfig.VERSION_NAME}"
        if (BuildConfig.DEBUG) {
            splash_server.visibility = View.VISIBLE
            splash_server.text = BuildConfig.BUILD_TYPE
        } else {
            splash_server.visibility = View.GONE
        }
    }

//    private fun getFireBaseToken() {
//        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                return@OnCompleteListener
//            }
//
//            // Get new Instance ID token
//            val token = task.result?.token
//            token?.let {
//                Preferences(this).fireBaseToken = it
//                Log.d("firebaseToken", "$it")
//                Log.d("isSubscribed", "${Preferences(this).isSubscribed}")
//            }
//        })
//    }


    private fun scheduleSplashScreen() {
        Handler().postDelayed({
            routeStart()
        }, splashDuration)
    }

    private fun routeStart() {
        if(preferences.accessToken.isEmpty()) {
            startActivity(Intent(this, IntroActivity::class.java))
        } else
            startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}
