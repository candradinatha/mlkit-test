package com.example.absensi.model.auth

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

data class UserData(

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("employee_id")
	val employeeId: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)

open class UserDataRealm: RealmObject() {

	@field:SerializedName("phone")
	var phone: String? = null

	@field:SerializedName("employee_id")
	var employeeId: String? = null

	@field:SerializedName("name")
	var name: String? = null

	@PrimaryKey
	@field:SerializedName("id")
	var id: Int? = null

	@field:SerializedName("email")
	var email: String? = null

}