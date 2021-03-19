package com.sergeenko.lookapp.models

import androidx.paging.DataSource
import androidx.room.*
import com.google.gson.Gson

@Entity
data class SocialResponse(
        @PrimaryKey val id: Int = 0,
        var message: String = "",
        var ok: Boolean = false,
        var errors: Errors = Errors(),
        var status: Int = 0,
        var data: Data = Data()
)

class DataConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): Data? {
        return Gson().fromJson(value, Data::class.java)
    }

    @TypeConverter
    fun dateToTimestamp(date: Data?): String? {
        return Gson().toJson(date)
    }
}


class ErrorsConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): Errors? {
        return Gson().fromJson(value, Errors::class.java)
    }

    @TypeConverter
    fun dateToTimestamp(date: Errors?): String? {
        return Gson().toJson(date)
    }
}

@Dao
interface SocialResponseDao {

    @Query("SELECT * FROM SocialResponse")
    fun get(): SocialResponse?

    @Update
    fun update(code: SocialResponse)

    @Insert
    fun insert(code: SocialResponse)

    @Delete
    fun delete(code: SocialResponse)
}