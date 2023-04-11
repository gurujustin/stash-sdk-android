/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.network.data

import com.google.gson.annotations.SerializedName

data class CreditCardAliasData(
    @SerializedName("ccExpiryMonth") val expiryMonth: String,
    @SerializedName("ccExpiryYear") val expiryYear: String,
    @SerializedName("ccType") val type: String,
    @SerializedName("ccMask") val mask: String
)