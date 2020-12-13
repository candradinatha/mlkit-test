package com.example.absensi.handler

import com.example.absensi.model.models.ModelsCheckResponse
import com.example.absensi.model.models.ModelsUploadResponse
import com.example.absensi.presenter.ModelsContract
import io.reactivex.Observable
import okhttp3.MultipartBody

class ModelsHandler: BaseHandler() {

    private val service = getClient().create(ModelsContract.Handler::class.java)

    fun checkModelsVersion(version: Int): Observable<ModelsCheckResponse> {
        return service.checkVersion(version)
    }

    fun uploadModels(
        trainFile: MultipartBody.Part,
        trainModelFile: MultipartBody.Part,
        labelFile: MultipartBody.Part
    ): Observable<ModelsUploadResponse> {
        return service.uploadModel(trainFile, trainModelFile, labelFile)
    }
}