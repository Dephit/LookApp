package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable
import androidx.paging.DataSource
import androidx.room.*

@Entity
data class Type(
    @PrimaryKey
    var id: Int = 0,
    var name: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Type> {
        override fun createFromParcel(parcel: Parcel): Type {
            return Type(parcel)
        }

        override fun newArray(size: Int): Array<Type?> {
            return arrayOfNulls(size)
        }
    }
}

@Dao
interface TypeDao {

    @Query("SELECT * FROM Type")
    fun getAll():  DataSource.Factory<Int, Type>

    @Query("SELECT * FROM Type")
    fun getAllInList():  List<Type>

    @Query("SELECT * FROM Type WHERE id == :id")
    fun getById(id: Int):  Type

    @Query("SELECT * FROM Type WHERE name == :name")
    fun getByName(name: String):  Type

    @Update
    fun update(code: Type)

    @Insert
    fun insert(code: Type)

    @Delete
    fun delete(code: Type)
}