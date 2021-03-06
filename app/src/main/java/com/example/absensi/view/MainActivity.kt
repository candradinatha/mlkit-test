package com.example.absensi.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.absensi.R
import com.example.absensi.common.Constants
import com.example.absensi.common.GlobalClass
import com.example.absensi.view.activity.LoginActivity
import com.example.absensi.view.fragment.AccountFragment
import com.example.absensi.view.fragment.AttendanceFragment
import com.example.absensi.view.fragment.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharePref = PreferenceManager.getDefaultSharedPreferences(this)
        sharePref.edit().putString("key_modelFileTensorFlow", "optimized_facenet.pb").apply()

        val tab = 0
        if (supportFragmentManager.findFragmentById(R.id.main_frame_layout) == null) {
            addFragment(HomeFragment(), Constants.ACTIVITY_MAIN)
        }
        bottom_navigation.run {
            setOnNavigationItemSelectedListener(this@MainActivity)
            menu.findItem(
                when (tab) {
                    0 -> R.id.menu_home
                    1 -> R.id.menu_attendance
                    2 -> R.id.menu_account
                    else -> R.id.menu_home
                }
            ).setChecked(true)
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_home -> {
                replaceFragment(HomeFragment())
            }
            R.id.menu_attendance -> {
                replaceFragment(AttendanceFragment())
            }
            R.id.menu_account -> {
                replaceFragment(AccountFragment())
            }
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            //.setCustomAnimations(R.anim.slide_in_down, R.anim.nothing, R.anim.nothing, R.anim.slide_out_up)
            .replace(R.id.main_frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            //.setCustomAnimations(R.anim.slide_in_down, R.anim.nothing, R.anim.nothing, R.anim.slide_out_up)
            .add(R.id.main_frame_layout, fragment)
            //.addToBackStack(tag)
            .commit()
    }

    private fun removeFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction
            //.setCustomAnimations(0, R.anim.slide_up)
            .remove(fragment)
            .commit()
    }

    fun intentLogout() {
        intent = Intent(this, LoginActivity::class.java)
        GlobalClass.userLoggedOut()
        startActivity(intent)
        finishAffinity()
    }
}
