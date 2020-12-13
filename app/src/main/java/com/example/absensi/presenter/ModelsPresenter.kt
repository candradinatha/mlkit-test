package com.example.absensi.presenter

import com.example.absensi.handler.ErrorHandler
import com.example.absensi.handler.ModelsHandler
import com.example.absensi.model.models.ModelsCheckResponse
import com.example.absensi.model.models.ModelsUploadResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class ModelsPresenter(val view: ModelsContract.View, val baseView: BaseContract.View): ModelsContract.Presenter, BaseContract.View {

    val handler = ModelsHandler()
    val basePresenter = BasePresenter(this)

    override fun checkVersion(version: Int) {
        handler.checkModelsVersion(version)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: ErrorHandler<ModelsCheckResponse>(basePresenter) {
                override fun onNext(t: ModelsCheckResponse) {
                    view.checkVersionResponse(t)
                }
            })
    }

    override fun uploadModels(
        trainFile: MultipartBody.Part,
        trainModelFile: MultipartBody.Part,
        label: MultipartBody.Part
    ) {
        handler.uploadModels(trainFile, trainModelFile, label)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ErrorHandler<ModelsUploadResponse>(basePresenter) {
                override fun onNext(t: ModelsUploadResponse) {
                    view.modelsUploadResponse(t)
                }
            })
    }

    override fun showError(title: String, message: String?) {
        baseView.showError(title, message)
    }
}