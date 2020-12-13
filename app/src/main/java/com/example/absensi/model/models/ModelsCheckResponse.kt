package com.example.absensi.model.models

import com.example.absensi.model.common.BaseResultData
import com.google.gson.annotations.SerializedName

class ModelsCheckResponse: BaseResultData(){

	@field:SerializedName("data")
	val data: ModelCheckData? = null
}

data class ModelCheckData(

	@field:SerializedName("need_update")
	val needUpdate: Boolean? = null
)
