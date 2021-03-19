package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable
import androidx.paging.DataSource
import androidx.room.*

@Entity
data class Brand(
        @PrimaryKey
        var id: Int = 0,
        var image: String? = "",
        var name: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString().toString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(image)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Brand> {
        override fun createFromParcel(parcel: Parcel): Brand {
            return Brand(parcel)
        }

        override fun newArray(size: Int): Array<Brand?> {
            return arrayOfNulls(size)
        }
    }
}

@Dao
interface BrandDao {

    @Query("SELECT * FROM Brand")
    fun getAll():  DataSource.Factory<Int, Brand>

    @Query("SELECT * FROM Brand")
    fun getAllInList():  List<Brand>

    @Query("SELECT * FROM Brand WHERE id == :id")
    fun getById(id: Int):  List<Brand>

    @Query("SELECT * FROM Brand WHERE name == :name")
    fun getByName(name: String):  List<Brand>

    @Update
    fun update(code: Brand)

    @Insert
    fun insert(code: Brand)

    @Delete
    fun delete(code: Brand)
}