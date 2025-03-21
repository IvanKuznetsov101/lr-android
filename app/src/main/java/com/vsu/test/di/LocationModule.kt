package com.vsu.test.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vsu.test.data.TokenManager
import com.vsu.test.data.api.EventService
import com.vsu.test.data.api.ProfileService
import com.vsu.test.data.repository.EventRepository
import com.vsu.test.data.repository.ProfileRepository
import com.vsu.test.domain.usecase.UpdateProfileCoordinatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}