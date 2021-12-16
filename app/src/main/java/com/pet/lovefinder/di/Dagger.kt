package com.pet.lovefinder.di

import com.pet.lovefinder.network.services.RegistrationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    fun provideRegistrationNetworkModule(): RegistrationService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://4text.app/")
            .build()
        return retrofit.create()
    }

}