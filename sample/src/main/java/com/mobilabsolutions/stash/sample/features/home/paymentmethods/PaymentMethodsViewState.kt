/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.paymentmethods

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.data.entities.User

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
data class PaymentMethodsViewState(
    val user: User? = null,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val stashInitialised: Boolean = false
) : MvRxState