package com.sergeenko.lookapp.models

import androidx.paging.DataSource
import androidx.room.*

@Entity
data class CountryCode(
    @PrimaryKey val id: Int = 0,
    val code: String = "+7",
    val country: String = "Russia",
    var isSelected: Boolean = false
)

@Dao
interface CountryCodeDao {
    @Query("SELECT * FROM CountryCode ")
    fun getAll():  DataSource.Factory<Int, CountryCode>

    @Query("SELECT * FROM CountryCode WHERE isSelected == 1")
    fun getSelected():  List<CountryCode>

    @Update
    fun update(code: CountryCode)

    @Insert
    fun insert(code: CountryCode)

    @Delete
    fun delete(code: CountryCode)
}

