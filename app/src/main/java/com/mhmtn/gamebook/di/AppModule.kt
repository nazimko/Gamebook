package com.mhmtn.gamebook.di

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
    fun providesGameRepo(
        api: GameAPI
    ) = GameRepo(api)


    @Singleton
    @Provides
    fun providesGameApi():GameAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(GameAPI::class.java)
    }


}