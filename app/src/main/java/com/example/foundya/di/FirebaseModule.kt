package com.example.foundya.di

import com.example.foundya.data.remote.api.ImageApi
import com.example.foundya.data.repository.AuthRepository
import com.example.foundya.data.repository.AuthRepositoryImpl
import com.example.foundya.data.repository.ImageRepository
import com.example.foundya.data.repository.ImageRepositoryImpl
import com.example.foundya.data.repository.NotificationRepository
import com.example.foundya.data.repository.NotificationRepositoryImpl
import com.example.foundya.data.repository.PostRepository
import com.example.foundya.data.repository.PostRepositoryImpl
import com.example.foundya.utils.FirebaseStorageConstants
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabaseInstance(): FirebaseDatabase{
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFireStoreInstance(): FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthInstance(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }


    @Singleton
    @Provides
    fun provideFirebaseStorageInstance(): StorageReference {
        return FirebaseStorage.getInstance().getReference(FirebaseStorageConstants.ROOT_DIRECTORY)
    }


    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(auth, firestore)
    }

    @Provides
    @Singleton
    fun providePostRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        messaging: FirebaseMessaging
    ): PostRepository = PostRepositoryImpl(firestore, auth, messaging)

    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = Firebase.messaging

    @Provides
    @Singleton
    fun provideNotificationRepository(
        firestore: FirebaseFirestore,
        messaging: FirebaseMessaging
    ): NotificationRepository = NotificationRepositoryImpl(firestore, messaging)

    @Provides
    @Singleton
    fun provideImageRepository(
        api: ImageApi,
        okHttpClient: OkHttpClient
    ): ImageRepository {
        return ImageRepositoryImpl(api, okHttpClient)
    }
}