package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

data class PostBodyItem(
    var content: Any? = null,
    var type: String? = ""
) : Parcelable {

    val imagePath: String = ""

    fun setPath(){

    }

    constructor(parcel: Parcel) : this(
        PostBodyItemConverter().readContent(parcel.readString()),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Gson().toJson(content))
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostBodyItem> {
        override fun createFromParcel(parcel: Parcel): PostBodyItem {
            return PostBodyItem(parcel)
        }

        override fun newArray(size: Int): Array<PostBodyItem?> {
            return arrayOfNulls(size)
        }
    }
}