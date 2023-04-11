/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.network.response

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
data class PaymentMethodListResponse(
    val paymentMethods: List<PaymentMethodResponse>
)