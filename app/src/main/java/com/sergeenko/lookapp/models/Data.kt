package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable

data class Data(
        var avatar: String = "",
        var email: String = "",
        var id: Int = 0,
        var new_user: Boolean = false,
        var phone: String? = "",
        var profile: Profile? = Profile(),
        var token: Token = Token(),
        var username: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readParcelable(Profile::class.java.classLoader)!!,
            parcel.readParcelable(Token::class.java.classLoader)!!,
            parcel.readString().toString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(avatar)
        parcel.writeString(email)
        parcel.writeInt(id)
        parcel.writeByte(if (new_user) 1 else 0)
        parcel.writeString(phone)
        parcel.writeParcelable(profile, flags)
        parcel.writeParcelable(token, flags)
        parcel.writeString(username)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}