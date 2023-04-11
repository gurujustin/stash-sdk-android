/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.mappers

import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.network.response.PaymentMethodListResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Singleton
class PaymentMethodListResponseToEntity @Inject constructor() : Mapper<PaymentMethodListResponse, List<PaymentMethod>> {
    override suspend fun map(from: PaymentMethodListResponse): List<PaymentMethod> {
        return from.paymentMethods.map {
            PaymentMethod(
                paymentMethodId = it.paymentMethodId,
                _type = it.type,
                // Credit Card
                mask = it.creditCardAliasData?.mask ?: "",
                expiryMonth = it.creditCardAliasData?.expiryMonth ?: "",
                expiryYear = it.creditCardAliasData?.expiryYear ?: "",
                _cardType = it.creditCardAliasData?.type ?: "UNKNOWN",
                // SEPA
                iban = it.sepaAliasData?.iban ?: "",
                // PayPal
                email = it.payPalAliasData?.email ?: ""
            )
        }
    }
}