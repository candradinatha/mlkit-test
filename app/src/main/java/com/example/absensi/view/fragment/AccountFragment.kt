package com.example.absensi.view.fragment


import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.absensi.R
import com.example.absensi.common.*
import com.example.absensi.model.auth.UserDataRealm
import com.example.absensi.model.models.ModelsCheckResponse
import com.example.absensi.model.models.ModelsUploadResponse
import com.example.absensi.presenter.ModelsContract
import com.example.absensi.presenter.ModelsPresenter
import com.example.absensi.view.AddPersonPreviewActivity
import com.example.absensi.view.BaseActivity
import com.example.absensi.view.MainActivity
import com.example.absensi.view.activity.TestActivity
import com.example.absensi.view.activity.TrainingActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.layout_toolbar_elevation_zero.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset


class AccountFragment : BaseFragment(), ModelsContract.View {

    private val realm = Realm.getDefaultInstance()
    private var userData: UserDataRealm? = null
    private var preferences = Preferences(GlobalClass.applicationContext()!!)
    private val modelsPresenter = ModelsPresenter(this, this)

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
        person_name_layout.editText?.addTextChangedListener(object : TextWatcher {
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
            if (isNameAlreadyUsed(FileHelper().trainingList, userData?.employeeId ?: ""))
                Toast.makeText(
                    context,
                    "This name is already used. Please choose another one.",
                    Toast.LENGTH_SHORT
                ).show()
            else
                intentAddPerson()
        }
        layout_logout.setOnClickListener {
            logout()
        }
        layout_download_model?.setOnClickListener {
            checkCurrentModelVersion()
        }

        layout_upload_model?.setOnClickListener {
            uploadModels()
        }

        layout_testing?.setOnClickListener {
            startActivity(Intent(context, TestActivity::class.java))
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

    private fun downloadTrain() {
//        svm train
        if (isFileExists(Constants.FILE_NAME_SVM_TRAIN)) {
            deleteFile(Constants.FILE_NAME_SVM_TRAIN)
            downloadFile(Constants.FILE_NAME_SVM_TRAIN)
        } else {
            downloadFile(Constants.FILE_NAME_SVM_TRAIN)
        }

//       svm train model
        if (isFileExists(Constants.FILE_NAME_SVM_TRAIN_MODEL)) {
            deleteFile(Constants.FILE_NAME_SVM_TRAIN_MODEL)
            downloadFile(Constants.FILE_NAME_SVM_TRAIN_MODEL)
        } else {
            downloadFile(Constants.FILE_NAME_SVM_TRAIN_MODEL)
        }

        // svm labelMap
        if (isFileExists(Constants.FILE_NAME_SVM_TRAIN_LABEL)) {
            deleteFile(Constants.FILE_NAME_SVM_TRAIN_LABEL)
            downloadFile(Constants.FILE_NAME_SVM_TRAIN_LABEL)
        } else {
            downloadFile(Constants.FILE_NAME_SVM_TRAIN_LABEL)
        }

        //json version
        if (isFileExists(Constants.FILE_NAME_VERSION)) {
            deleteFile(Constants.FILE_NAME_VERSION)
            downloadFile(Constants.FILE_NAME_VERSION)
        } else {
            downloadFile(Constants.FILE_NAME_VERSION)
        }
    }


    private fun isFileExists(fileName: String): Boolean {
        val file = File(FileHelper.SVM_PATH + fileName)
        return file.exists()
    }

    private fun deleteFile(fileName: String) {
        val file = File(FileHelper.SVM_PATH + fileName)
        file.delete()
    }

    private fun downloadFile(fileName: String) {
        var url = ""
        when (fileName) {
            Constants.FILE_NAME_SVM_TRAIN -> {
                url =
                    "${Utilities.getApiUrlForHttps()}${Constants.API_ACTION_MODEL}/${Constants.API_ACTION_TRAIN}/${Constants.API_ACTION_DOWNLOAD}"
            }
            Constants.FILE_NAME_SVM_TRAIN_LABEL -> {
                url =
                    "${Utilities.getApiUrlForHttps()}${Constants.API_ACTION_MODEL}/${Constants.API_ACTION_LABEL}/${Constants.API_ACTION_DOWNLOAD}"
            }
            Constants.FILE_NAME_SVM_TRAIN_MODEL -> {
                url =
                    "${Utilities.getApiUrlForHttps()}${Constants.API_ACTION_MODEL}/${Constants.API_ACTION_TRAIN_MODEL}/${Constants.API_ACTION_DOWNLOAD}"
            }
            Constants.FILE_NAME_VERSION -> {
                url =
                    "${Utilities.getApiUrlForHttps()}${Constants.API_ACTION_MODEL}/${Constants.API_ACTION_VERSION}/${Constants.API_ACTION_DOWNLOAD}"
            }
        }
        val req = DownloadManager.Request(Uri.parse(url))
        req.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_PICTURES,
            "facerecognition/data/SVM/$fileName"
        )
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val dm = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(req)
    }

    private fun getCurrentVersion(): Int? {
        var version: Int? = null
        try {
            val yourFile = File(FileHelper.SVM_PATH+"version.json")
            val stream = FileInputStream(yourFile)
            var jsonStr: String? = null
            try {
                val fc: FileChannel = stream.getChannel()
                val bb: MappedByteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())
                jsonStr = Charset.defaultCharset().decode(bb).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                stream.close()
            }

            val jsonObj = JSONObject(jsonStr)

            // Getting data JSON Array nodes
            version = jsonObj.getInt("version")

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return version
    }

    private fun uploadModels() {
        context?.let {
            val trainFile = File(FileHelper.SVM_PATH+"svm_train")
            val trainModelFile = File(FileHelper.SVM_PATH+"svm_train_model")
            val labelFile = File(FileHelper.SVM_PATH+"labelMap_train")
            val trainPart = MultipartBody.Part.createFormData("train", trainFile.name, RequestBody.create(null, trainFile))
            val trainModelPart = MultipartBody.Part.createFormData("train_model", trainModelFile.name, RequestBody.create(null, trainModelFile))
            val labelPart = MultipartBody.Part.createFormData("label", labelFile.name, RequestBody.create(null, labelFile))
            Utilities.showProgressDialog(it)
            modelsPresenter.uploadModels(trainPart, trainModelPart, labelPart)
        }
    }

    private fun showModelCheckResponse(needUpdate: Boolean) {
        if (needUpdate) {
            (activity as BaseActivity).showAlertDialog(
                SweetAlertDialog.WARNING_TYPE,
                title = context?.getString(R.string.model_update_available),
                confirmTitle = getString(R.string.download_new_model),
                confirmListener = {
                    it.dismissWithAnimation()
                    downloadTrain()
                }
            )
        } else {
            (activity as BaseActivity).showAlertDialog(
                SweetAlertDialog.SUCCESS_TYPE,
                title = context?.getString(R.string.model_up_to_date),
                confirmTitle = getString(R.string.dialog_ok),
                confirmListener = {
                    it.dismissWithAnimation()
                }
            )
        }
    }

    private fun checkCurrentModelVersion() {
        val currentVersion = getCurrentVersion() ?: 0
        context?.run {
            Utilities.showProgressDialog(this)
        }
        modelsPresenter.checkVersion(currentVersion)
    }

    override fun checkVersionResponse(response: ModelsCheckResponse) {
        Utilities.hideProgressDialog()
        response.data?.needUpdate?.run {
            showModelCheckResponse(this)
        }
    }

    override fun modelsUploadResponse(response: ModelsUploadResponse) {
        Utilities.hideProgressDialog()
        (activity as BaseActivity).uploadSuccessDialog()
    }

}
