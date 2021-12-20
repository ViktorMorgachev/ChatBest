package com.pet.chat.di

import com.pet.chat.events.InternalEventsProvider
import com.pet.chat.network.services.UploadFileService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Provides
    fun providesBaseUrl(): String = "https://185.26.121.63:3001/"

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient().newBuilder().addInterceptor(HttpLoggingInterceptor()).build())
        .build()

    @Provides
    @Singleton
    fun provideUploadFileNetworkModule(retrofit: Retrofit): UploadFileService =
        retrofit.create(UploadFileService::class.java)

    @Provides
    @Singleton
    fun provideInternalEvents(): InternalEventsProvider {
        return InternalEventsProvider()
    }


}