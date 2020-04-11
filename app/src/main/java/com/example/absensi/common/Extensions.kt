package com.example.absensi.common

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.absensi.R
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults


fun View.setVisibility(value: Boolean) {
    this.visibility = if (value) View.VISIBLE else View.GONE
}

fun <T>MutableList<T>.saveAll() {
    val realm = Realm.getDefaultInstance()
    for (item in this) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(item as RealmObject)
        realm.commitTransaction()
    }
}

fun <T>RealmResults<T>.deleteAll() {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction {
        this.deleteAllFromRealm()
    }
}

fun RealmObject.save() {
    val realm = Realm.getDefaultInstance()
    realm.beginTransaction()
    realm.copyToRealmOrUpdate(this)
    realm.commitTransaction()
}

fun RealmObject.saveWithoutPrimary() {
    val realm = Realm.getDefaultInstance()
    realm.beginTransaction()
    realm.copyToRealm(this)
    realm.commitTransaction()
}

fun RealmObject.delete() {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction {
        this.deleteFromRealm()
    }
}

fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.snackBarActionClose(message: String, view: View) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        .setAction(getString(R.string.snackbar_dissmiss)) { }
        .setActionTextColor(R.color.colorPrimary.getColor())
        .show()
}

fun Int.getColor() : Int {
    return ContextCompat.getColor(GlobalClass.applicationContext()!!,this)
}

fun Long.toTwoDigits(): String {
    return String.format("%02d", this)
}

fun Int.toTwoDigits(): String {
    return String.format("%02d", this)
}

fun String?.abbreviateString(maxLength: Int): String {
    return if (this == null){
        ""
    } else {
        return if (this.length <= maxLength)
            this
        else
            "${this.substring(0, maxLength - 2)}.."
    }
}

