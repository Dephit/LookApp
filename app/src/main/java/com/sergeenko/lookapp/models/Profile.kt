package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable

data class Profile(
    var chest: Int? = null,
    var gender: String? = null,
    var height: Int? = null,
    var hips: Int? = null,
    var waist: Int? = null,
    var weight: Int? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(chest)
        parcel.writeString(gender)
        parcel.writeValue(height)
        parcel.writeValue(hips)
        parcel.writeValue(waist)
        parcel.writeValue(weight)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}