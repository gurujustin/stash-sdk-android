/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.network.request

import com.google.gson.annotations.SerializedName
import com.mobilabsolutions.stash.sample.network.data.CreditCardAliasData
import com.mobilabsolutions.stash.sample.network.data.PayPalAliasData
import com.mobilabsolutions.stash.sample.network.data.SepaAliasData

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
data class CreatePaymentMethodRequest(
    val aliasId: String,
    val type: String,
    val userId: String,
    @SerializedName("ccData") val creditCardAliasData: CreditCardAliasData? = null,
    @SerializedName("sepaData") val sepaAliasData: SepaAliasData? = null,
    @SerializedName("payPalData") val payPalAliasData: PayPalAliasData? = null
)