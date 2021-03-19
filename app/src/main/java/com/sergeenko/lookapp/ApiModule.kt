package com.sergeenko.lookapp

import android.content.Context
import androidx.room.Room
import com.sergeenko.lookapp.models.BrandDao
import com.sergeenko.lookapp.models.CodeDao
import com.sergeenko.lookapp.models.CurrencyDao
import com.sergeenko.lookapp.models.TypeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApiModule {

    @Singleton
    @Provides
    fun provideApi(): Api {
        val client = OkHttpClient.Builder()
            .callTimeout(60L, TimeUnit.SECONDS)
            .connectTimeout(60L, TimeUnit.SECONDS)
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(Api::class.java)
    }

    @Singleton
    @Provides
    fun provideRepository(api: Api, database: AppDatabase): Repository {
        return  RepositoryImpl(api, database)
    }

}

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(app, AppDatabase::class.java, "look_db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): CountryCodeDao {
        return appDatabase.countryCodeDao()
    }

    @Provides
    fun provideCodeDao(appDatabase: AppDatabase): CodeDao {
        return appDatabase.codeDao()
    }

    @Provides
    fun provideBrandDao(appDatabase: AppDatabase): BrandDao {
        return appDatabase.brandDao()
    }

    @Provides
    fun provideCurrencyDao(appDatabase: AppDatabase): CurrencyDao {
        return appDatabase.currencyDao()
    }

    @Provides
    fun provideTypeDao(appDatabase: AppDatabase): TypeDao {
        return appDatabase.typeDao()
    }
}

/*
@InstallIn(ApplicationComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): CountryCodeDao {
        return appDatabase.countryCodeDao()
    }
}

*/

