package com.sergeenko.lookapp.models

import android.os.Parcel
import android.os.Parcelable
import androidx.paging.DataSource
import androidx.room.*

@Entity
data class Currency(
    @PrimaryKey
    var id: Int = 0,
    var name: String? = "",
    var sign: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(sign)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Currency> {
        override fun createFromParcel(parcel: Parcel): Currency {
            return Currency(parcel)
        }

        override fun newArray(size: Int): Array<Currency?> {
            return arrayOfNulls(size)
        }
    }
}

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM Currency")
    fun getAll():  DataSource.Factory<Int, Currency>

    @Query("SELECT * FROM Currency")
    fun getAllInList():  List<Currency>

    @Query("SELECT * FROM Currency WHERE id == :id")
    fun getById(id: Int):  Currency

    @Query("SELECT * FROM Currency WHERE name == :name")
    fun getByName(name: String):  Currency

    @Update
    fun update(code: Currency)

    @Insert
    fun insert(code: Currency)

    @Delete
    fun delete(code: Currency)
}