package com.fakhry.transjakarta.core.networking.di

import com.fakhry.transjakarta.core.networking.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api-v3.mbta.com/"
    private const val TIMEOUT_SECONDS = 30L
    private const val HEADER_API_KEY = "x-api-key"
    private const val HEADER_ACCEPT = "Accept"
    private const val CONTENT_TYPE_JSON_API = "application/vnd.api+json"

    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val requestBuilder =
                chain
                    .request()
                    .newBuilder()
                    .addHeader(HEADER_ACCEPT, CONTENT_TYPE_JSON_API)

            // Add API key header if available
            val apiKey = BuildConfig.MBTA_API_KEY
            if (apiKey.isNotBlank()) {
                requestBuilder.addHeader(HEADER_API_KEY, apiKey)
            }

            chain.proceed(requestBuilder.build())
        }.connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType()),
        ).build()
}
