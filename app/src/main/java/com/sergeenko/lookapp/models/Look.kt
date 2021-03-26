package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File


data class Look(
    var count_dislikes: Int = 0,
    var count_likes: Int = 0,
    var count_comments: Int = 0,
    var id: Int = 0,
    var preview: String? = null,
    var images: List<Image> = listOf(),
    var user: Data = Data(),
    var is_dislike: Boolean = false,
    var is_like: Boolean = false,
    var is_favorite: Boolean = false,
    var title: String = "",
    var type: String = "",
    var favorite_id: Int = 0,
    var body: List<PostBodyItem> = listOf(),
    var is_claim: Boolean = false,
    var created: String = ""
) : Parcelable {

    var previewPath = ""

    fun setPath(){

    }


    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.createTypedArrayList(Image)!!,
        parcel.readParcelable(Data::class.java.classLoader)!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.createTypedArrayList(PostBodyItem)!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(count_dislikes)
        parcel.writeInt(count_likes)
        parcel.writeInt(count_comments)
        parcel.writeInt(id)
        parcel.writeString(preview)
        parcel.writeTypedList(images)
        parcel.writeParcelable(user, flags)
        parcel.writeByte(if (is_dislike) 1 else 0)
        parcel.writeByte(if (is_like) 1 else 0)
        parcel.writeByte(if (is_favorite) 1 else 0)
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeInt(favorite_id)
        parcel.writeTypedList(body)
        parcel.writeByte(if (is_claim) 1 else 0)
        parcel.writeString(created)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Look> {
        override fun createFromParcel(parcel: Parcel): Look {
            return Look(parcel)
        }

        override fun newArray(size: Int): Array<Look?> {
            return arrayOfNulls(size)
        }
    }
}