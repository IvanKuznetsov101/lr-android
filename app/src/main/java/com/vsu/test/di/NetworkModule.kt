package com.vsu.test.di

import android.content.ContentResolver
import android.content.Context
import android.util.Log
import coil.Coil
import coil.ImageLoader
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.vsu.test.data.api.ProfileService
import com.vsu.test.BuildConfig
import com.vsu.test.data.TokenAuthenticator
import com.vsu.test.data.TokenManager
import com.vsu.test.data.api.AuthService
import com.vsu.test.data.api.EventService
import com.vsu.test.data.api.ImageService
import com.vsu.test.data.api.LightRoomService
import com.vsu.test.data.interceptors.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    @Named("MainOkHttpClient")
    fun provideMainOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor) // Логирует все запросы, включая Coil
        .addInterceptor(authInterceptor) // Добавляет JWT-токен
        .authenticator(tokenAuthenticator) // Обновляет токен при 401
        .build()

    @Provides
    @Singleton
    fun provideImageLoader(
        @Named("MainOkHttpClient") okHttpClient: OkHttpClient,
        @ApplicationContext context: Context
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient(okHttpClient) // Используем существующий клиент без изменений
            .crossfade(true)
            .build()
    }

    // OkHttpClient для аутентификации (без TokenAuthenticator)
    @Provides
    @Singleton
    @Named("AuthOkHttpClient")
    fun provideAuthOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

    // Основной Retrofit для всех сервисов, кроме AuthService
    @Provides
    @Singleton
    @Named("MainRetrofit")
    fun provideMainRetrofit(@Named("MainOkHttpClient") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_LOCAL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    // Retrofit для AuthService
    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(@Named("AuthOkHttpClient") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_LOCAL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, JsonDeserializer<LocalDate> { json, _, _ ->
                LocalDate.parse(json.asString, formatter)
            })
            .setDateFormat("yyyy-MM-dd")
            .create()
    }

    @Provides
    @Singleton
    fun provideAuthService(@Named("AuthRetrofit") retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideProfileService(@Named("MainRetrofit") retrofit: Retrofit) =
        retrofit.create(ProfileService::class.java)

    @Provides
    @Singleton
    fun provideLightRoomService(@Named("MainRetrofit") retrofit: Retrofit) =
        retrofit.create(LightRoomService::class.java)

    @Provides
    @Singleton
    fun provideEventService(@Named("MainRetrofit") retrofit: Retrofit) =
        retrofit.create(EventService::class.java)

    @Provides
    @Singleton
    fun provideImageService(@Named("MainRetrofit") retrofit: Retrofit) =
        retrofit.create(ImageService::class.java)

    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }


}