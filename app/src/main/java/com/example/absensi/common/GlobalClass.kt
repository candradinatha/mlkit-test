package com.example.absensi.common

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import io.realm.Realm
import io.realm.RealmConfiguration

class GlobalClass : Application() {
    lateinit var context: Context

    init {
        instance = this
    }
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Realm.init(context)
        initRealm()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initRealm() {
        val realmConfiguration = RealmConfiguration.Builder()
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }


    companion object {
        private var instance: GlobalClass? = null

        fun applicationContext() : Context? {
            return instance?.applicationContext
        }

        fun userLoggedOut() {
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction { realm1 -> realm1.deleteAll() }
            realm.close()
            Preferences(instance!!.applicationContext).userLoggedOut()
        }
    }
}
