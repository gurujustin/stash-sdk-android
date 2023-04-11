package com.mobilabsolutions.stash.sample.features.home

import com.airbnb.mvrx.MvRxState

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 16-08-2019.
 */
data class HomeActivityViewState(
    val loading: Boolean = false
) : MvRxState