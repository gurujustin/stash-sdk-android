/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.checkout

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.stash.sample.data.resultentities.CartWithProduct

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
data class CheckoutViewState(
    val loading: Boolean = false,
    val cartItems: List<CartWithProduct> = emptyList(),
    val totalAmount: Int = 0,
    val showEmptyView: Boolean = false,
    val paymentCompleted: Boolean = false
) : MvRxState