package com.example.absensi.view.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory
import com.example.absensi.R
import com.example.absensi.common.Constants
import com.example.absensi.common.Preferences
import com.example.absensi.common.setVisibility
import com.example.absensi.model.attendance.today.TodayAttendanceResponse
import com.example.absensi.model.auth.UserDataRealm
import com.example.absensi.presenter.*
import com.example.absensi.view.BaseActivity
import io.realm.Realm
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Rect
import java.io.File

class RecognitionActivity : BaseActivity(), CvCameraViewListener2, AttendanceContract.View, DetectionContract.View {

    private lateinit var preferences: Preferences
    private var mRecognitionView: CustomCameraView? = null
    private var fh: FileHelper? = null
    private var rec: Recognition? = null
    private var ppF: PreProcessorFactory? = null
    private var progressBar: ProgressBar? = null
    private var front_camera = false
    private var night_portrait = false
    private var exposure_compensation = 0
    private val presenter = AttendancePresenter(this, this)
    private var attendanceId = 0
    private var attendanceAction = 0
    private var realm = Realm.getDefaultInstance()
    private var employeeId = getUserData()?.employeeId
    private var detectionPresenter = DetectionPresenter(this)

    companion object {
        private const val TAG = "Recognition"

        init {
            if (!OpenCVLoader.initDebug()) { // Handle initialization error
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "called onCreate")
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.recognition_layout)
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        fh = FileHelper()
        val folder = File(FileHelper.getFolderPath())
        if (folder.mkdir() || folder.isDirectory) {
            Log.i(
                TAG,
                "New directory for photos created"
            )
        } else {
            Log.i(
                TAG,
                "Photos directory already existing"
            )
        }
        mRecognitionView = findViewById<View>(R.id.RecognitionView) as CustomCameraView
        // Use camera which is selected in settings
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        front_camera = sharedPref.getBoolean("key_front_camera", true)
        night_portrait = true
        exposure_compensation = Integer.valueOf(sharedPref.getString("key_exposure_compensation", "20")!!)
        if (front_camera) {
            mRecognitionView!!.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT)
        } else {
            mRecognitionView!!.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK)
        }
        mRecognitionView!!.visibility = SurfaceView.VISIBLE
        mRecognitionView!!.setCvCameraViewListener(this)
        mRecognitionView!!.setMaxFrameSize(640, 480)

        attendanceId = intent.getIntExtra(Constants.INTENT_ATTENDANCE_ID, 0)
        attendanceAction = intent.getIntExtra(Constants.INTENT_ATTENDANCE_ACTION, 0)

        preferences = Preferences(this)
    }

    public override fun onPause() {
        super.onPause()
        if (mRecognitionView != null) mRecognitionView!!.disableView()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mRecognitionView != null) mRecognitionView!!.disableView()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        if (night_portrait) {
            mRecognitionView!!.setNightPortrait()
        }
        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100) mRecognitionView!!.setExposure(
            exposure_compensation
        )
    }

    override fun onCameraViewStopped() {}

    override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
        val imgRgba = inputFrame.rgba()
        val img = Mat()
        imgRgba?.copyTo(img)
        val images = ppF!!.getProcessedImage(img, PreProcessorFactory.PreprocessingMode.RECOGNITION)
        var faces = ppF!!.facesForRecognition
        // Selfie / Mirror mode
        if (front_camera) {
            Core.flip(imgRgba, imgRgba, 1)
        }
        return if (images == null || images.size == 0 || faces == null || faces.size == 0 || images.size != faces.size) { // skip
            imgRgba?: Mat()
        } else {
            faces = MatOperation.rotateFaces(imgRgba, faces, ppF!!.angleForRecognition)
            for (i in faces.indices) {
//                recognize(images[i], imgRgba, faces[i])
//                val label = rec!!.recognize(images[i], "")
//                MatOperation.drawRectangleAndLabelOnPreview(
//                    imgRgba,
//                    faces[i],
//                    label,
//                    front_camera
//                )
//                if (label == employeeId) {
//                    Handler(Looper.getMainLooper()).post(object : Runnable {
//                        override fun run() {
//                           checkInOrOut(attendanceId)
//                        }
//                    })
//                }
            }
            imgRgba?: Mat()
        }
    }

    public override fun onResume() {
        super.onResume()
        ppF = PreProcessorFactory(applicationContext)
        val handler = Handler(Looper.getMainLooper())
        val t = Thread(Runnable {
            handler.post { progressBar!!.visibility = View.VISIBLE }
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            //                String algorithm = sharedPref.getString("key_classification_method", getResources().getString(R.string.eigenfaces));
            val algorithm = getString(R.string.tensorflow)
            rec = RecognitionFactory.getRecognitionAlgorithm(
                applicationContext,
                Recognition.RECOGNITION,
                algorithm
            )
            handler.post { progressBar!!.visibility = View.GONE }
        })
        t.start()
        // Wait until Eigenfaces loading thread has finished
        try {
            t.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mRecognitionView!!.enableView()
    }

    //region contract view

    override fun checkInResponse(response: TodayAttendanceResponse) {
        preferences.afterCheckInSuccess = true
        isLoading(false)
        finish()
    }

    override fun checkOutResponse(response: TodayAttendanceResponse) {
        preferences.afterCheckOutSuccess = true
        isLoading(false)
        finish()
    }

    override fun recognitionResult(recognitionResult: RecognitionResultData) {
        MatOperation.drawRectangleAndLabelOnPreview(
            recognitionResult.imgRgba,
            recognitionResult.face,
            recognitionResult.label,
            front_camera
        )
//                if (label == employeeId) {
//                    Handler(Looper.getMainLooper()).post(object : Runnable {
//                        override fun run() {
//                           checkInOrOut(attendanceId)
//                        }
//                    })
//                }
    }

    override fun showError(title: String, message: String?) {
        super.showError(title, message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun getTodayAttendance(response: TodayAttendanceResponse) {}

    // endregion

    // region function
    private fun isLoading(isLoading: Boolean) {
        if (isLoading) progressBar?.setVisibility(true) else progressBar?.setVisibility(false)
    }


    private fun checkInOrOut(id: Int) {
        isLoading(true)
        if (attendanceAction == Constants.CHECK_IN) {
            presenter.checkIn(id)
        } else if (attendanceAction == Constants.CHECK_OUT) {
            presenter.checkOut(id)
        }
    }

    private fun getUserData(): UserDataRealm? {
        return realm.where(UserDataRealm::class.java).findFirst()
    }

    private fun recognize(image: Mat, imgRgba: Mat, face: Rect) {
        rec?.let {
            detectionPresenter.startTask(it, image, imgRgba, face)
        }
    }

    //endregion


}
