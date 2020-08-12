package com.example.absensi.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.absensi.R
import com.example.absensi.common.CameraSource
import com.example.absensi.facedetection.FaceDetectionProcessor
import kotlinx.android.synthetic.main.activity_face_detection.*
import org.opencv.android.OpenCVLoader
import java.io.IOException


private const val REQUEST_CODE_PERMISSIONS = 10
// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class FaceDetectionActivity : AppCompatActivity() {

    private lateinit var cameraSource: CameraSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_detection)
        OpenCVLoader.initDebug()


        if (allPermissionsGranted()) {
            cameraSource = CameraSource(this, fireFaceOverlay)
            cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT)
            createCameraSource()
            startCameraSource()
        }else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }


    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                createCameraSource()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun createCameraSource() {
        cameraSource.setMachineLearningFrameProcessor(FaceDetectionProcessor())
    }

    private fun startCameraSource() {
        cameraSource.let {
            try {
                firePreview?.start(cameraSource, fireFaceOverlay)
            } catch (e: IOException) {
                cameraSource.release()
            }
        }
    }

}
