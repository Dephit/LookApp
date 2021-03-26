package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable

data class Image(
    var id: Int = 0,
    var marks: List<Mark> = listOf(),
    var url: String = "",
    var total: Int = 0,
) : Parcelable {
    val imagePath: String = ""

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.createTypedArrayList(Mark)!!,
            parcel.readString().toString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeTypedList(marks)
        parcel.writeString(url)
        parcel.writeInt(total)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}