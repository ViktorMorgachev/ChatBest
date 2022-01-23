package com.pet.chat.di

import com.pet.chat.helpers.SingleLiveEvent
import com.pet.chat.network.EventFromServer
import com.pet.chat.network.data.ViewState
import com.pet.chat.network.services.UploadFileService
import com.pet.chat.providers.MultipleChatProviderImpl
import com.pet.chat.providers.UsersProviderImpl
import com.pet.chat.providers.ViewStateProviderImpl
import com.pet.chat.providers.interfaces.*
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

    @Singleton
    @Provides
    fun provideUserProvider(): UsersProvider{
        return UsersProviderImpl(users = MutableStateFlow(listOf()))
    }

    @Provides
    fun provideViewStateProvider(): ViewStateProvider {
        return ViewStateProviderImpl(viewState = SingleLiveEvent())
    }

    @Provides
    @Singleton
    fun provideMultipleChatProvider(): MultipleChatProviderImpl{
      return  MultipleChatProviderImpl(chats = MutableStateFlow(listOf()))
    }

    @Provides
    @Singleton
    fun provideEventFromServerProvider(): EventFromServerProvider{
        return EventFromServerProviderImpl(events = MutableStateFlow(EventFromServer.NO_INITIALIZED))
    }



}

