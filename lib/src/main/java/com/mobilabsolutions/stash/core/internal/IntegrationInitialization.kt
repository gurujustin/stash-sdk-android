/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal

import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.internal.psphandler.Integration

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface IntegrationInitialization {
    val enabledPaymentMethodTypes: Set<PaymentMethodType>

    fun initialize(stashComponent: StashComponent, url: String = ""): Integration

    fun initializedOrNull(): Integration?
}