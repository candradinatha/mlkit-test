package com.example.absensi

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import kotlinx.android.synthetic.main.activity_camera.*

private const val REQUEST_CODE_PERMISSIONS = 10
// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class CameraActivity : AppCompatActivity(), LifecycleOwner {

    val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
        .build()
    val faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(highAccuracyOpts)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        if (allPermissionsGranted()) {
            view_finder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Every time the provided texture view changes, recompute layout
        view_finder .addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }

    }

    private fun startCamera() {
        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(CameraX.LensFacing.FRONT)
            setTargetResolution(Size(640, 480))
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        var analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // In our analysis, we care more about the latest image than
            // analyzing *every* image
            setLensFacing(CameraX.LensFacing.FRONT)
            setImageReaderMode(
                ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        val blinkDetector = ImageAnalysis(analyzerConfig).apply {
            analyzer = ImageProcessor()
        }

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = view_finder.parent as ViewGroup
            parent.removeView(view_finder)
            parent.addView(view_finder, 0)

            view_finder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview, blinkDetector)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = view_finder.width / 2f
        val centerY = view_finder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(view_finder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        view_finder.setTransform(matrix)
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                view_finder.post { startCamera() }
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    inner class ImageProcessor : ImageAnalysis.Analyzer {
        private val TAG = javaClass.simpleName

        private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
        }

        override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
            val mediaImage = image?.image
            val imageRotation = degreesToFirebaseRotation(rotationDegrees)
            if (mediaImage != null) {
                val visionImage = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
                // Pass image to an ML Kit Vision API
                // ...
                faceDetector.detectInImage(visionImage)
                    .addOnSuccessListener { faces ->
                        for (face in faces) {
                            Log.d("faces", "founded")
                            val bounds = face.boundingBox
                            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                            // nose available):
                            val leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)
                            leftEar?.let {
                                val leftEarPos = leftEar.position
                            }

                            // If contour detection was enabled:
                            val leftEyeContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points
                            val upperLipBottomContour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points

                            // If classification was enabled:
                            if (face.smilingProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                val smileProb = face.smilingProbability
                            }
                            if (face.rightEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                val rightEyeOpenProb = face.rightEyeOpenProbability
                            }

                            // If face tracking was enabled:
                            if (face.trackingId != FirebaseVisionFace.INVALID_ID) {
                                val id = face.trackingId
                            }
                        }
                    }
                    .addOnFailureListener{ e ->

                        Log.d("faces", "not founded")
                        Toast.makeText(this@CameraActivity, "$e", Toast.LENGTH_LONG).show()
                    }
            }
//            image?.image?.let {
//                // Extract the mediaImage from the ImageProxy
//                val visionImage = FirebaseVisionImage.fromMediaImage(it, rotationDegrees)
//                faceDetector.detectInImage(visionImage)
//                    .addOnSuccessListener { faces ->
//                        // Returns an array containing all the detected faces
//                        faces.forEach { face ->
//                            // Loop through the array and detect features
//                            Log.e(TAG, "Smiling probability ${face.smilingProbability}")
//                            Log.e(TAG, "Left eye open probability ${face.leftEyeOpenProbability}")
//                            Log.e(TAG, "Right eye open probability ${face.rightEyeOpenProbability}")
//                            Log.e(TAG, "Tracking id ${face.trackingId}")
//                        }
//                    }
//                    .addOnFailureListener {
//                        it.printStackTrace()
//                    }
//            }
        }
    }
}

