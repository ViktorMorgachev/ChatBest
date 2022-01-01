package com.pet.chat.di

import com.pet.chat.network.services.UploadFileService
import com.pet.chat.providers.MultipleChatProviderImpl
import com.pet.chat.ui.ChatItemInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
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
    fun provideMultipleChatProvider(): MultipleChatProviderImpl{
      return  MultipleChatProviderImpl(chats = MutableStateFlow(listOf()))
    }


}
