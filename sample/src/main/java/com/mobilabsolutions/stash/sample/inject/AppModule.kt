/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.inject

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.preference.PreferenceManager
import com.mobilabsolutions.stash.sample.BuildConfig
import com.mobilabsolutions.stash.sample.SampleApplication
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
@Module(includes = [
    AppModuleBinds::class
])
object AppModule {

    @JvmStatic
    @Provides
    fun provideContext(application: SampleApplication): Context = application.applicationContext

    @JvmStatic
    @Singleton
    @Provides
    fun provideRxSchedulers(): AppRxSchedulers = AppRxSchedulers(
        io = Schedulers.io(),
        computation = Schedulers.computation(),
        main = AndroidSchedulers.mainThread()
    )

    @JvmStatic
    @Singleton
    @Provides
    fun provideCoroutineDispatchers() = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main
    )

    @JvmStatic
    @Provides
    @Singleton
    @Named("cache")
    fun provideCacheDir(application: SampleApplication): File = application.cacheDir

    @JvmStatic
    @Provides
    @ProcessLifetime
    fun provideLongLifetimeScope(): CoroutineScope {
        return ProcessLifecycleOwner.get().lifecycle.coroutineScope
    }

    @JvmStatic
    @Provides
    @Singleton
    @Named("app_version")
    fun provideAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    @JvmStatic
    @Provides
    @Singleton
    @Named("sdk_version")
    fun provideLibVersion(): String {
        return com.mobilabsolutions.stash.core.BuildConfig.VERSION_NAME
    }

    @JvmStatic
    @Named("app")
    @Provides
    @Singleton
    fun provideAppPreferences(application: SampleApplication): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }
}