package com.example.absensi.presenter

import com.example.absensi.common.Constants
import com.example.absensi.model.models.ModelsCheckResponse
import com.example.absensi.model.models.ModelsUploadResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

interface ModelsContract {

    interface View {
        fun checkVersionResponse(response: ModelsCheckResponse)
        fun modelsUploadResponse(response: ModelsUploadResponse)
    }

    interface Presenter {
        fun checkVersion(version: Int)
        fun uploadModels(trainFile: MultipartBody.Part, trainModelFile: MultipartBody.Part, label: MultipartBody.Part)
    }

    interface Handler {

        @GET("${Constants.API_ACTION_MODEL}/${Constants.API_ACTION_CHECK_VERSION}/{version}")
        fun checkVersion(
            @Path(value = "version", encoded = true) version: Int
        ): Observable<ModelsCheckResponse>

        @Multipart
        @POST("models/upload")
        fun uploadModel(
            @Part trainFile: MultipartBody.Part,
            @Part trainModelFile: MultipartBody.Part,
            @Part label: MultipartBody.Part
        ): Observable<ModelsUploadResponse>

    }
}