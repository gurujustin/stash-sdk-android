package com.mobilabsolutions.stash.sample.features.home.info

import android.content.Context
import com.mobilabsolutions.stash.sample.R
import com.mobilabsolutions.stash.sample.inject.PerActivity
import javax.inject.Inject
import javax.inject.Named

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-08-2019.
 */
class InfoTextCreator @Inject constructor(
    @PerActivity private val context: Context,
    @Named("app_version") private val appVersion: String,
    @Named("sdk_version") private val sdkVersion: String
) {
    fun appVersionText(): CharSequence {
        return context.getString(R.string.app_version, appVersion)
    }

    fun sdkVersionText(): CharSequence {
        return context.getString(R.string.lib_version, sdkVersion)
    }

    fun backendVersionText(): CharSequence {
        return context.getString(R.string.backend_version)
    }
}