/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.appinitializers

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.mobilabsolutions.stash.sample.BuildConfig
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class CrashlyticsInitializer @Inject constructor() : AppInitializer {
    override fun init(application: Application) {
        if (!BuildConfig.DEBUG) {
            Fabric.with(application, Crashlytics())
        }
    }
}
