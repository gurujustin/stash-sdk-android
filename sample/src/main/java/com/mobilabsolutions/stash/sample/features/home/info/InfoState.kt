package com.mobilabsolutions.stash.sample.features.home.info

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.stash.sample.data.SamplePreference

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 11-09-2019.
 */
data class InfoState(
    val creditCardPref: SamplePreference.Psp? = null,
    val sepaPref: SamplePreference.Psp? = null,
    val stashInitialized: Boolean = false
) : MvRxState