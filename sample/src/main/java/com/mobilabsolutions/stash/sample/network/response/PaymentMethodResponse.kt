/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.network.response

import com.google.gson.annotations.SerializedName
import com.mobilabsolutions.stash.sample.network.data.CreditCardAliasData
import com.mobilabsolutions.stash.sample.network.data.PayPalAliasData
import com.mobilabsolutions.stash.sample.network.data.SepaAliasData

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
data class PaymentMethodResponse(
    val paymentMethodId: String,
    val type: String,
    @SerializedName("ccData") val creditCardAliasData: CreditCardAliasData?,
    @SerializedName("sepaData") val sepaAliasData: SepaAliasData?,
    @SerializedName("payPalData") val payPalAliasData: PayPalAliasData?
)