/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.api.backend.v1

import com.google.gson.annotations.SerializedName
import com.mobilabsolutions.stash.core.model.BillingData

data class AliasExtra(
    @SerializedName("ccConfig")
    val creditCardConfig: CreditCardConfig? = null,
    val sepaConfig: SepaConfig? = null,
    val paymentMethod: String,
    val payPalConfig: PayPalConfig? = null,
    val personalData: BillingData? = null,
    val payload: String? = null,
    val channel: String = "Android"
)

data class CreditCardConfig(
    val ccExpiry: String,
    val ccMask: String,
    val ccType: String,
    val ccHolderName: String?,
    val nonce: String = "",
    val deviceData: String = "",
    val encryptedCardNumber: String? = null,
    val encryptedExpiryMonth: String? = null,
    val encryptedExpiryYear: String? = null,
    val encryptedSecurityCode: String? = null
)

data class SepaConfig(
    val iban: String? = null,
    val bic: String? = null,
    val name: String? = null,
    val lastname: String? = null,
    val street: String? = null,
    val zip: String? = null,
    val city: String? = null,
    val country: String? = null
)

data class PayPalConfig(
    val nonce: String = "",
    val deviceData: String = ""
)
