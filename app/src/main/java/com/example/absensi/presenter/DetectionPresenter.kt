package com.example.absensi.presenter

import ch.zhaw.facerecognitionlibrary.Recognition.Recognition
import kotlinx.coroutines.*
import org.opencv.core.Mat
import org.opencv.core.Rect
import kotlin.coroutines.CoroutineContext

class DetectionPresenter(val view: DetectionContract.View): CoroutineScope {

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun cancel() {
        job.cancel()
    }

    fun startTask(recognition: Recognition, image: Mat, imgRgba: Mat, face: Rect) = launch {
        val result = doSomeBackgroundWork(recognition, image)
        val recognitionResult = RecognitionResultData(
            imgRgba,
            face, result
        )
        doUIStuff(recognitionResult)
    }

    private suspend fun doSomeBackgroundWork(recognition: Recognition, image: Mat): String = withContext(Dispatchers.IO) {
        return@withContext recognition.recognize(image, "")
    }

    private fun doUIStuff(recognitionResult: RecognitionResultData) {
        view.recognitionResult(recognitionResult)
    }
}

interface DetectionContract {
    interface View {
        fun recognitionResult(recognitionResult: RecognitionResultData)
    }
}

data class RecognitionResultData(
    val imgRgba: Mat,
    val face: Rect,
    val label: String
)