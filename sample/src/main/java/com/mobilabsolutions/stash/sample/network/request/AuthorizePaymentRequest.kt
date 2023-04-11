/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.network.request

data class AuthorizePaymentRequest(
    val amount: Int,
    val currency: String,
    val paymentMethodId: String,
    val reason: String
)