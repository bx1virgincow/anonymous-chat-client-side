package com.example.anonymouschat.di

import com.example.anonymouschat.data.remote.api.UserApiService
import com.example.anonymouschat.data.remote.websocket.WebSocketClient
import com.example.anonymouschat.data.remote.websocket.WebSocketClientImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

/** Module for network related dependencies */

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    /** Bind WebsocketClient interface */
    @Binds
    @Singleton
    abstract fun bindWebSocketClient(
        impl: WebSocketClientImpl
    ): WebSocketClient

    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkProvidersModule {

        @Provides
        @Singleton
        fun provideUserApiService(okHttpClient: OkHttpClient): UserApiService {
            return UserApiService(okHttpClient)
        }
    }
}