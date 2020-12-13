package com.example.absensi.model.models

import com.example.absensi.model.common.BaseResultData
import com.google.gson.annotations.SerializedName

class ModelsUploadResponse: BaseResultData() {

	@field:SerializedName("data")
	val data: Data? = null
}

data class Data(

	@field:SerializedName("label")
	val label: String? = null,

	@field:SerializedName("version")
	val version: Int? = null,

	@field:SerializedName("train_model")
	val trainModel: String? = null,

	@field:SerializedName("train")
	val train: String? = null
)
