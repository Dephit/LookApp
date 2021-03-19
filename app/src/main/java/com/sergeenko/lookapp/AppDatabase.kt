package com.sergeenko.lookapp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sergeenko.lookapp.models.*

@Database(entities = [CountryCode::class, Code::class, Brand::class, Currency::class, Type::class, SocialResponse::class], version = 6)
@TypeConverters(DataConverter::class, ErrorsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryCodeDao(): CountryCodeDao
    abstract fun codeDao(): CodeDao
    abstract fun brandDao(): BrandDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun typeDao(): TypeDao
    abstract fun socialResponseDao(): SocialResponseDao
}