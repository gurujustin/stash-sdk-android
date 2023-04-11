/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.psphandler

import com.mobilabsolutions.stash.core.model.BillingData
import com.mobilabsolutions.stash.core.model.CreditCardData
import com.mobilabsolutions.stash.core.model.SepaData

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class RegistrationRequest(
    val standardizedData: StandardizedData,
    val additionalData: AdditionalRegistrationData = AdditionalRegistrationData()
)

interface StandardizedData {
    val aliasId: String
}

data class CreditCardRegistrationRequest(
    val creditCardData: CreditCardData,
    val billingData: BillingData = BillingData(),
    override val aliasId: String
) : StandardizedData

data class SepaRegistrationRequest(
    val sepaData: SepaData,
    val billingData: BillingData = BillingData(),
    override val aliasId: String
) : StandardizedData

data class PayPalRegistrationRequest(override val aliasId: String) : StandardizedData