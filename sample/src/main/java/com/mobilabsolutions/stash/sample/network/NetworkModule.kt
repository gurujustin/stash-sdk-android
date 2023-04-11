/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.mobilabsolutions.stash.sample.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Module
object NetworkModule {
    private const val BASE_URL = "https://payment-dev.mblb.net/"
    private const val DEMO_BASE_URL = "https://payment-demo.mblb.net/"
    private const val DISK_CACHE_SIZE = (10 * 1024 * 1024).toLong()
    private const val READ_TIME_OUT = 15.toLong()
    private const val CONNECT_TIME_OUT = 30.toLong()

    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @Named("cache") cacheDir: File
    ): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
        builder.apply {
            if (BuildConfig.DEBUG) {
                addNetworkInterceptor(StethoInterceptor())
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
            readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            cache(Cache(File(cacheDir, "sample_app_cache"), DISK_CACHE_SIZE))
        }
        return builder.build()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    @JvmStatic
    @Provides
    fun provideSampleMerchantService(retrofit: Retrofit): SampleMerchantService =
        retrofit.create(SampleMerchantService::class.java)
}