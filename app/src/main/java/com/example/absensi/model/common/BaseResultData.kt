package com.example.absensi.model.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class BaseResultData {
    @Expose
    var meta: Meta? = null
    @Expose
    var links: Links? = null
}

class Meta {

    @field:SerializedName("path")
    val path: String? = null

    @field:SerializedName("per_page")
    val perPage: Int? = null

    @field:SerializedName("total")
    val total: Int? = null

    @field:SerializedName("last_page")
    val lastPage: Int? = null

    @field:SerializedName("from")
    val from: Int? = null

    @field:SerializedName("to")
    val to: Int? = null

    @field:SerializedName("current_page")
    val currentPage: Int? = null
}

class Links {

    @field:SerializedName("next")
    val next: String? = null

    @field:SerializedName("last")
    val last: String? = null

    @field:SerializedName("prev")
    val prev: String? = null

    @field:SerializedName("first")
    val first: String? = null
}

class APIError {
    val error: ErrorData =
        ErrorData()
}

class ErrorData {
    val code: Int = 0
    val title: String = ""
    val errors: List<ErrorItem> = listOf()
}

class ErrorItem {
    val title: String = ""
    val message: String = ""
}

