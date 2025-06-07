package com.example.foundya.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.example.foundya.data.repository.NotificationRepository
import com.example.foundya.data.use_case.ClaimUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideResources(application: Application): Resources = application.resources

    @Provides
    @Singleton
    fun provideClaimUseCase(
        notificationRepository: NotificationRepository,
        auth: FirebaseAuth
    ): ClaimUseCase {
        return ClaimUseCase(notificationRepository, auth)
    }
}