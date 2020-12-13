package com.example.absensi.view.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.absensi.R
import com.example.absensi.common.Constants
import com.example.absensi.common.Preferences
import com.example.absensi.common.Utilities
import com.example.absensi.common.toast
import com.example.absensi.model.models.ModelsCheckResponse
import com.example.absensi.model.models.ModelsUploadResponse
import com.example.absensi.presenter.ModelsContract
import com.example.absensi.presenter.ModelsPresenter
import com.example.absensi.view.BaseActivity
import kotlinx.android.synthetic.main.activity_intro.*
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset

class IntroActivity : BaseActivity(), ModelsContract.View {
    
    private val modelPresenter = ModelsPresenter(this, this)
    private lateinit var preferences: Preferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        preferences = Preferences(this)
        card_instant_attendance?.setOnClickListener {
            checkModelVersion()
        }

        card_login_register?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (preferences.afterCheckInSuccess) {
            preferences.afterCheckInSuccess = false
        }
        if (preferences.afterCheckOutSuccess) {
            preferences.afterCheckOutSuccess = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.CHECK_IN_OUT_RESULT) {
            val name = data?.getStringExtra(Constants.ARGS_INTENT_NAME) ?: ""
            val time = data?.getStringExtra(Constants.ARGS_INTENT_TIME) ?: ""
            val isCheckIn = data?.getBooleanExtra(Constants.ARGS_INTENT_IS_CHECK_IN, false)
            isCheckIn?.let {
                if (it)
                    checkInSuccessDialog(name, time)
                else
                    checkOutSuccessDialog(name, time)
            }
        }
    }

    override fun checkVersionResponse(response: ModelsCheckResponse) {
        Utilities.hideProgressDialog()
        showModelCheckResponse(response.data?.needUpdate ?: true)
    }

    override fun modelsUploadResponse(response: ModelsUploadResponse) {
    }
    
    private fun checkModelVersion() {
        Utilities.showProgressDialog(this)
        val version = getCurrentVersion() ?: 0
        modelPresenter.checkVersion(version)
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

    private fun showModelCheckResponse(needUpdate: Boolean) {
        if (needUpdate) {
            showAlertDialog(
                SweetAlertDialog.WARNING_TYPE,
                title = getString(R.string.model_update_available),
                confirmTitle = getString(R.string.download_new_model),
                confirmListener = {
                    it.dismissWithAnimation()
                    downloadTrain()
                }
            )
        } else {
            with(Intent(this, RecognitionActivity::class.java)) {
                this.putExtra(Constants.IS_INSTANT, true)
                startActivityForResult(this, Constants.CHECK_IN_OUT_RESULT)
            }
        }
    }

    private fun downloadTrain() {
//        svm train
        if (isFileExists(Constants.FILE_NAME_SVM_TRAIN)) {
            deleteModelFile(Constants.FILE_NAME_SVM_TRAIN)
            downloadFile(Constants.FILE_NAME_SVM_TRAIN)
        } else {
            downloadFile(Constants.FILE_NAME_SVM_TRAIN)
        }

//       svm train model
        if (isFileExists(Constants.FILE_NAME_SVM_TRAIN_MODEL)) {
            deleteModelFile(Constants.FILE_NAME_SVM_TRAIN_MODEL)
            downloadFile(Constants.FILE_NAME_SVM_TRAIN_MODEL)
        } else {
            downloadFile(Constants.FILE_NAME_SVM_TRAIN_MODEL)
        }

        // svm labelMap
        if (isFileExists(Constants.FILE_NAME_SVM_TRAIN_LABEL)) {
            deleteModelFile(Constants.FILE_NAME_SVM_TRAIN_LABEL)
            downloadFile(Constants.FILE_NAME_SVM_TRAIN_LABEL)
        } else {
            downloadFile(Constants.FILE_NAME_SVM_TRAIN_LABEL)
        }

        //json version
        if (isFileExists(Constants.FILE_NAME_VERSION)) {
            deleteModelFile(Constants.FILE_NAME_VERSION)
            downloadFile(Constants.FILE_NAME_VERSION)
        } else {
            downloadFile(Constants.FILE_NAME_VERSION)
        }
    }


    private fun isFileExists(fileName: String): Boolean {
        val file = File(FileHelper.SVM_PATH + fileName)
        return file.exists()
    }

    private fun deleteModelFile(fileName: String) {
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
        val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(req)
    }

}