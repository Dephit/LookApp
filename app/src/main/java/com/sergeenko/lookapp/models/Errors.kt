package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable

data class Errors(
    var username: List<String> = listOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createStringArrayList()!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Errors> {
        override fun createFromParcel(parcel: Parcel): Errors {
            return Errors(parcel)
        }

        override fun newArray(size: Int): Array<Errors?> {
            return arrayOfNulls(size)
        }
    }
}