package com.example.anonymouschat.di

import android.content.Context
import com.example.anonymouschat.data.remote.websocket.StompMessageHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/** Hilt module for app-wide dependencies */

@Module
@InstallIn(SingletonComponent::class)
object AppModule{
    /** OkHttpClient */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient{
        val loggingInterceptor = HttpLoggingInterceptor()
            .apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    /** stomp messages handler */
    @Provides
    @Singleton
    fun provideStompMessageHandler(): StompMessageHandler{
        return StompMessageHandler()
    }

    /** application context */
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context{
        return context
    }
}