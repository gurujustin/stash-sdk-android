/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.uicomponents

import com.mobilabsolutions.stash.core.PaymentMethodType

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PaymentMethodDefinition(
    val methodId: String,
    val pspIdentifier: String,
    val paymentMethodType: PaymentMethodType

)
