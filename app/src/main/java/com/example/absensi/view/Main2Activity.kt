package com.example.absensi.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.SurfaceView
import ch.zhaw.facerecognitionlibrary.Helpers.MatName
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory
import com.example.absensi.R
import kotlinx.android.synthetic.main.activity_main2.*
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect
import java.util.*

class Main2Activity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {

    val TIME = 0
    val MANUALLY = 1
    var method = MANUALLY
    private var timerDiff: Long = 500
    private var lastTime: Long = 0
    private var ppF: PreProcessorFactory? = null
    val capturePressed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        addPersonPreview.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT)
        addPersonPreview.visibility = SurfaceView.VISIBLE
        addPersonPreview.setCvCameraViewListener(this)

        lastTime = Date().time

        val maxCameraViewWidth = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_width", "640")!!)
        val maxCameraViewHeight = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_height", "480")!!)
        addPersonPreview.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight)
    }


    override fun onCameraViewStarted(width: Int, height: Int) {
    }

    override fun onCameraViewStopped() {
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        val imgRgba = inputFrame!!.rgba()
        val imgCopy = Mat()
        imgRgba?.copyTo(imgCopy)
        // Selfie / Mirror mode

        Core.flip(imgRgba, imgRgba, 1)

        val time = Date().time
        if (method == MANUALLY || method == TIME && lastTime + timerDiff < time) {
            lastTime = time

            // Check that only 1 face is found. Skip if any or more than 1 are found.
            val images = ppF?.getCroppedImage(imgCopy)
            if (images != null && images.size == 1) {
                val img = images.get(0)
                if (img != null) {
                    var faces: Array<Rect>? = ppF?.getFacesForRecognition()
                    //Only proceed if 1 face has been detected, ignore if 0 or more than 1 face have been detected
                    if (faces != null && faces.size == 1) {
                        faces =
                            MatOperation.rotateFaces(imgRgba, faces, ppF!!.getAngleForRecognition())
                        if (method == MANUALLY && capturePressed || method == TIME) {
//                            val m = MatName(name + "_" + total, img)
//                            if (folder == "Test") {
//                                val wholeFolderPath = fh.TEST_PATH + name + "/" + subfolder
//                                File(wholeFolderPath).mkdirs()
//                                fh.saveMatToImage(m, wholeFolderPath + "/")
//                            } else {
//                                val wholeFolderPath = fh.TRAINING_PATH + name
//                                File(wholeFolderPath).mkdirs()
//                                fh.saveMatToImage(m, wholeFolderPath + "/")
//                            }
//
//                            for (i in faces!!.indices) {
//                                MatOperation.drawRectangleAndLabelOnPreview(
//                                    imgRgba,
//                                    faces[i],
//                                    total.toString(),
//                                    front_camera
//                                )
//                            }
//
//                            total++
//
//                            // Stop after numberOfPictures (settings option)
//                            if (total >= numberOfPictures) {
//                                val intent =
//                                    Intent(applicationContext, AddPersonActivity::class.java)
//                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                startActivity(intent)
//                            }
//                            capturePressed = false
                            for (i in faces!!.indices) {
                                MatOperation.drawRectangleOnPreview(imgRgba, faces[i], true)
                            }
                        } else {
                            for (i in faces!!.indices) {
                                MatOperation.drawRectangleOnPreview(imgRgba, faces[i], true)
                            }
                        }
                    }
                }
            }
        }

        return imgRgba
    }

    override fun onResume() {
        super.onResume()
        ppF = PreProcessorFactory(this)
        addPersonPreview.enableView()
    }
}
