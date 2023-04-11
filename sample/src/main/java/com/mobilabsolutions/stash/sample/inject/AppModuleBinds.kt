/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.inject

import android.app.Application
import com.mobilabsolutions.stash.sample.SampleApplication
import com.mobilabsolutions.stash.sample.appinitializers.AppInitializer
import com.mobilabsolutions.stash.sample.appinitializers.CrashlyticsInitializer
import com.mobilabsolutions.stash.sample.appinitializers.DebugToolsInitializer
import com.mobilabsolutions.stash.sample.appinitializers.EpoxyInitializer
import com.mobilabsolutions.stash.sample.appinitializers.RxAndroidInitializer
import com.mobilabsolutions.stash.sample.data.SamplePreference
import com.mobilabsolutions.stash.sample.data.SamplePreferenceImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Module
abstract class AppModuleBinds {
    @Binds
    abstract fun provideApplication(bind: SampleApplication): Application

    @Binds
    @IntoSet
    abstract fun provideDebugToolsInitializer(bind: DebugToolsInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideCrashlyticsInitializer(bind: CrashlyticsInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideRxAndroidInitializer(bind: RxAndroidInitializer): AppInitializer

    @Binds
    @IntoSet
    abstract fun provideEpoxyInitializer(bind: EpoxyInitializer): AppInitializer

    @Singleton
    @Binds
    abstract fun providePreferences(bind: SamplePreferenceImpl): SamplePreference
}