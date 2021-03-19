package com.sergeenko.lookapp.models

import androidx.paging.DataSource
import androidx.room.*
import java.io.Serializable

@Entity
data class Code(
    @PrimaryKey
    var id: Int = 0,
    var dialCode: String? = "",
    var flag: String? = "",
    var isoCode: String? = "",
    var mask: String? = "",
    var name: String? = "",
    var name_ru: String? = "",
    var isSelected: Boolean = false
): Serializable

@Dao
interface CodeDao {

    @Query("SELECT * FROM Code order by case when name_ru='Россия' then -1 else name_ru end") //ORDER BY name_ru ASC
    fun getAll():  DataSource.Factory<Int, Code>

    @Query("SELECT * FROM Code WHERE isSelected == 1")
    fun getSelected():  List<Code>

    @Update
    fun update(code: Code)

    @Insert
    fun insert(code: Code)

    @Delete
    fun delete(code: Code)
}