package com.example.absensi.facedetection

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.util.Log
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.example.absensi.R
import com.example.absensi.common.CameraImageGraphic
import com.example.absensi.common.FrameMetadata
import com.example.absensi.common.GlobalClass
import com.example.absensi.common.GraphicOverlay
import io.reactivex.Observable
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.IOException

/** Face Detector Demo.  */
class FaceDetectionProcessor(res: Resources) : VisionProcessorBase<List<FirebaseVisionFace>>() {

    private val detector: FirebaseVisionFaceDetector
    private val rec: Recognition
    private val overlayBitmap: Bitmap
    private var label = ""
    private var successIndex = 0

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .build()

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        overlayBitmap = BitmapFactory.decodeResource(res, R.drawable.clown_nose)
        rec = RecognitionFactory.getRecognitionAlgorithm(GlobalClass.applicationContext(), Recognition.RECOGNITION,"TensorFlow with SVM or KNN")

    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionFace>> {
        return detector.detectInImage(image)
    }

    override fun onSuccess(
        originalCameraImage: Bitmap?,
        results: List<FirebaseVisionFace>,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {
        successIndex += 1
        graphicOverlay.clear()
        val imageGraphic = CameraImageGraphic(graphicOverlay, originalCameraImage)
        graphicOverlay.add(imageGraphic)
        for (i in results.indices) {
            val face = results[i]
            val rgbaMat = Mat(originalCameraImage!!.height, originalCameraImage.width,  CvType.CV_8UC4)
            val cameraFacing = frameMetadata.cameraFacing
//            val faceGraphic = FaceGraphic(graphicOverlay, face, cameraFacing, overlayBitmap)
            if ((successIndex % 10) ==0) {
                label = rec.recognize(rgbaMat, "")
                Log.d(TAG, "resultindex $successIndex")
            }
            val faceGraphic = FaceGraphic(graphicOverlay, face, cameraFacing, null, label)
            graphicOverlay.add(faceGraphic)
        }
        graphicOverlay.postInvalidate()
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {

        private const val TAG = "FaceDetectionProcessor"
    }
}