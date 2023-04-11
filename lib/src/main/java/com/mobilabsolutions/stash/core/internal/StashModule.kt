/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mobilabsolutions.stash.core.BuildConfig
import com.mobilabsolutions.stash.core.ExtraAliasInfo
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.UiCustomizationManager
import com.mobilabsolutions.stash.core.exceptions.ExceptionMapper
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import com.mobilabsolutions.stash.core.internal.api.backend.PayoneSpecificData
import com.mobilabsolutions.stash.core.internal.api.backend.ProviderSpecificData
import com.mobilabsolutions.stash.core.internal.api.backend.RuntimeTypeAdapterFactory
import com.mobilabsolutions.stash.core.internal.psphandler.Integration
import dagger.Module
import dagger.Provides
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
open class StashModule(
    private val publishableKey: String,
    private val mobilabUrl: String,
    private val applicationContext: Application,
    private val integrationInitializers: Map<IntegrationInitialization, Set<PaymentMethodType>>,
    private val testMode: Boolean
) {

    companion object {
        const val DEFAULT_SHARED_PREFERENCES_NAME = "DefaultSharedPreferences"
        const val MOBILAB_TIMEOUT = 60L
        val TIMEOUT_UNIT = TimeUnit.SECONDS
    }

    private var uiCustomizationManager: UiCustomizationManager? = null

    @Provides
    fun provideHttpLoggingInterceptor(@Named("isLogging") isLogging: Boolean): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if (isLogging) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideMobilabApi(
        @Named("mobilabHttpClient")
        mobilabBackendOkHttpClient: OkHttpClient,
        @Named("mobilabBackendGsonConverterFactory")
        gsonConverterFactory: GsonConverterFactory,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory
    ): MobilabApi {
        val mobilabBackendRetrofit = Retrofit.Builder()
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(mobilabBackendOkHttpClient)
                .baseUrl(mobilabUrl)
                .build()

        return mobilabBackendRetrofit.create(MobilabApi::class.java)
    }

    @Provides
    @Singleton
    @Named("mobilabHttpClient")
    fun provideMobilabHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        sslSupportPackage: SslSupportPackage
    ): OkHttpClient {

        val mobilabBackendOkHttpClientBuilder = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                            .addHeader("Publishable-Key", publishableKey)
                            .addHeader("User-Agent", "Android-${BuildConfig.VERSION_CODE}-${BuildConfig.VERSION_NAME}")
                    if (testMode) {
                        requestBuilder.addHeader("PSP-Test-Mode", "true")
                    }
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .addInterceptor(httpLoggingInterceptor)
                .addNetworkInterceptor(StethoInterceptor())
                .connectTimeout(MOBILAB_TIMEOUT, TIMEOUT_UNIT)
                .readTimeout(MOBILAB_TIMEOUT, TIMEOUT_UNIT)
                .writeTimeout(MOBILAB_TIMEOUT, TIMEOUT_UNIT)
        if (sslSupportPackage.useCustomSslSocketFactory) {
            mobilabBackendOkHttpClientBuilder.sslSocketFactory(sslSupportPackage.sslSocketFactory!!, sslSupportPackage.x509TrustManager!!)
            val connectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .build()
            mobilabBackendOkHttpClientBuilder.connectionSpecs(listOf(connectionSpec))
        }
        return mobilabBackendOkHttpClientBuilder.build()
    }

    @Provides
    @Named("isLogging")
    fun provideLoggingFlag(): Boolean {
        return BuildConfig.DEBUG
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory() = GsonConverterFactory.create()

    @Named("mobilabBackendGsonConverterFactory")
    @Provides
    @Singleton
    fun provideBackendGsonConverterFactory(): GsonConverterFactory {
        val runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(ProviderSpecificData::class.java, "psp")
                .registerSubtype(PayoneSpecificData::class.java, "payone")

        val gson = GsonBuilder()
                .registerTypeAdapterFactory(runtimeTypeAdapterFactory)
                .create()
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun providesRxJava2CallAdapter() = RxJava2CallAdapterFactory.create()

    @Provides
    @Singleton
    fun provideDefaultGson(): Gson {
        val extraInfoTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(ExtraAliasInfo::class.java, "extraType")
                .registerSubtype(ExtraAliasInfo.CreditCardExtraInfo::class.java, "CC")
                .registerSubtype(ExtraAliasInfo.SepaExtraInfo::class.java, "SEPA")
                .registerSubtype(ExtraAliasInfo.PaypalExtraInfo::class.java, "PAYPAL")

        return GsonBuilder()
                .registerTypeAdapterFactory(extraInfoTypeAdapterFactory)
                .create()
    }

    @Provides
    @Singleton
    fun provideDefaultSharedPreferences(): SharedPreferences = applicationContext.getSharedPreferences(
            DEFAULT_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    @Provides
    @Singleton
    fun provideExceptionMapper(gson: Gson): ExceptionMapper {
        return ExceptionMapper(gson)
    }

    @Provides
    @Singleton
    fun provideApplicationContext(): Application {
        return applicationContext
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return applicationContext
    }

    @Provides
    @Singleton
    fun provideUiCustomizationManager(gson: Gson, sharedPreferences: SharedPreferences): UiCustomizationManager {
        if (uiCustomizationManager == null) {
            uiCustomizationManager = UiCustomizationManager(gson, sharedPreferences)
        }
        return uiCustomizationManager as UiCustomizationManager
    }

    @Provides
    @Singleton
    fun providePspIntegrationsRegistered(): Map<Integration, Set<PaymentMethodType>> {
        return integrationInitializers.filter { it.key.initializedOrNull() != null }
                .mapKeys { it.key.initializedOrNull() as Integration }
    }
}