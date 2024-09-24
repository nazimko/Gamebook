package com.mhmtn.gamebook.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mhmtn.gamebook.data.GameDao
import com.mhmtn.gamebook.data.GameDatabase
import com.mhmtn.gamebook.repo.GameRepo
import com.mhmtn.gamebook.service.GameAPI
import com.mhmtn.gamebook.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesGameRepo(api: GameAPI,dao: GameDao) = GameRepo(api,dao)

    @Singleton
    @Provides
    fun providesGameApi():GameAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(GameAPI::class.java)
    }


    @Provides
    @Singleton
    fun provideGameDatabase(app:Application):GameDatabase {
        return Room.databaseBuilder(
            app,
            GameDatabase::class.java,
            "GameDatabase"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(database: GameDatabase):GameDao{
        return database.dao
    }
}