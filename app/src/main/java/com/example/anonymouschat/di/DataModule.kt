package com.example.anonymouschat.di

import com.example.anonymouschat.data.local.datastore.UserPreferences
import com.example.anonymouschat.data.local.datastore.UserPreferencesImpl
import com.example.anonymouschat.data.repository.ChatRepositoryImpl
import com.example.anonymouschat.data.repository.MessageRepositoryImpl
import com.example.anonymouschat.data.repository.UserRepositoryImpl
import com.example.anonymouschat.domain.repository.ChatRepository
import com.example.anonymouschat.domain.repository.MessageRepository
import com.example.anonymouschat.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Module for data-layer */

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferences(
        impl: UserPreferencesImpl
    ): UserPreferences

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        impl: MessageRepositoryImpl
    ): MessageRepository
}