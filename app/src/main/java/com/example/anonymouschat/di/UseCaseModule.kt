package com.example.anonymouschat.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Module for UseCases */

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {}