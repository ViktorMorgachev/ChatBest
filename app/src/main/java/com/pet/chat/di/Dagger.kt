package com.pet.chat.di

import com.pet.chat.network.services.UploadFileService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    fun provideUploadFileNetworkModule(): UploadFileService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://185.26.121.63:3001/")
            .client(OkHttpClient().newBuilder().addInterceptor(HttpLoggingInterceptor()).build())
            .build()
        return retrofit.create()
    }

}