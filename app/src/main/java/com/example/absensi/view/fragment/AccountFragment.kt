package com.example.absensi.view.fragment


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.absensi.R
import com.example.absensi.common.GlobalClass
import com.example.absensi.common.Preferences
import com.example.absensi.common.setVisibility
import com.example.absensi.model.auth.UserDataRealm
import com.example.absensi.view.AddPersonPreviewActivity
import com.example.absensi.view.MainActivity
import com.example.absensi.view.activity.TrainingActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.layout_toolbar_elevation_zero.*
import java.io.File

class AccountFragment : BaseFragment() {

    private val realm = Realm.getDefaultInstance()
    private var userData: UserDataRealm? = null
    private var preferences = Preferences(GlobalClass.applicationContext()!!)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarHome(toolbar)
        toolbar_title.text = getText(R.string.account_menu_title)

        userData = getUserData()
        setView()
        person_name_layout.editText?.addTextChangedListener(object : TextWatcher{
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                person_name_layout.error = null
                person_name_layout.isErrorEnabled = false
            }
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })
        button_training.setOnClickListener {
            startActivity(Intent(context, TrainingActivity::class.java))
        }
        button_action_add_face.setOnClickListener {
            if (isNameAlreadyUsed(FileHelper().trainingList, userData?.employeeId?:""))
                Toast.makeText(context, "This name is already used. Please choose another one.", Toast.LENGTH_SHORT).show()
            else
                intentAddPerson()
        }
        layout_logout.setOnClickListener {
            logout()
        }
    }

    private fun intentAddPerson() {
        val intent = Intent(context, AddPersonPreviewActivity::class.java)
        intent.putExtra("Name", userData?.employeeId)
        intent.putExtra("Folder", "Training")
        startActivity(intent)
    }

    private fun setView() {
        userData?.let {
            admin_name.text = it.name
            admin_phone_read.text = "${getString(R.string.indonesian_dial_code)}${it.phone}"
            admin_email_read.text = it.email
            admin_employee_id_read.text = it.employeeId
        }
        layout_sex.setVisibility(false)
    }

    private fun isNameAlreadyUsed(list: Array<File>?, name: String): Boolean {
        var used = false
        if (list != null && list.size > 0) {
            for (person in list) {
                // The last token is the name --> Folder name = Person name
                val tokens = person.absolutePath.split("/").toTypedArray()
                val foldername = tokens[tokens.size - 1]
                if (foldername == name) {
                    used = true
                    break
                }
            }
        }
        return used
    }

    private fun getUserData(): UserDataRealm? {
        return realm.where(UserDataRealm::class.java).findFirst()
    }

    private fun logout() {
        (activity as MainActivity).run {
            showAlertDialog(
                SweetAlertDialog.WARNING_TYPE,
                "Logout",
                "Are you sure to logout this session?",
            "Logout",
                "Cancel",
                //confirm
                {
                    it.dismissWithAnimation()
                    intentLogout()
                },
                //cancel
                {
                    it.dismissWithAnimation()
                }
            )
        }
    }

}
