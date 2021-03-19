package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable

data class Mark(
    var brand: String = "",
    var brand_image: String? = "",
    var coordinate_x: Int = 0,
    var coordinate_y: Int = 0,
    var currency: String = "",
    var id: Int = 0,
    var label: String = "",
    var price: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString().toString(),
            parcel.readInt(),
            parcel.readString().toString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(brand)
        parcel.writeString(brand_image)
        parcel.writeInt(coordinate_x)
        parcel.writeInt(coordinate_y)
        parcel.writeString(currency)
        parcel.writeInt(id)
        parcel.writeString(label)
        parcel.writeInt(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mark> {
        override fun createFromParcel(parcel: Parcel): Mark {
            return Mark(parcel)
        }

        override fun newArray(size: Int): Array<Mark?> {
            return arrayOfNulls(size)
        }
    }
}