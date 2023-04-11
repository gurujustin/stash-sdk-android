/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.paymentmethod

import com.mobilabsolutions.stash.sample.data.RetrofitRunner
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.data.entities.PaymentType
import com.mobilabsolutions.stash.sample.data.entities.Result
import com.mobilabsolutions.stash.sample.data.mappers.PaymentMethodListResponseToEntity
import com.mobilabsolutions.stash.sample.network.SampleMerchantService
import com.mobilabsolutions.stash.sample.network.data.CreditCardAliasData
import com.mobilabsolutions.stash.sample.network.data.PayPalAliasData
import com.mobilabsolutions.stash.sample.network.data.SepaAliasData
import com.mobilabsolutions.stash.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.stash.sample.network.request.CreatePaymentMethodRequest
import com.mobilabsolutions.stash.sample.network.response.AuthorizePaymentResponse
import com.mobilabsolutions.stash.sample.network.response.CreatePaymentMethodResponse
import java.util.UUID
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
class RemotePaymentMethodDataSource @Inject constructor(
    private val retrofitRunner: RetrofitRunner,
    private val sampleMerchantService: SampleMerchantService,
    private val paymentMethodListResponseToEntity: PaymentMethodListResponseToEntity
) {
    suspend fun addPaymentMethod(
        userId: String,
        aliasId: String,
        paymentMethod: PaymentMethod
    ): Result<CreatePaymentMethodResponse> {
        var request = CreatePaymentMethodRequest(
            aliasId = aliasId,
            type = paymentMethod._type,
            userId = userId
        )

        request = when (paymentMethod.type) {
            PaymentType.CC -> request.copy(
                creditCardAliasData = CreditCardAliasData(
                    type = paymentMethod._cardType,
                    mask = paymentMethod.mask,
                    expiryMonth = paymentMethod.expiryMonth,
                    expiryYear = paymentMethod.expiryYear
                )
            )
            PaymentType.SEPA -> request.copy(
                sepaAliasData = SepaAliasData(paymentMethod.iban)
            )
            PaymentType.PAY_PAL -> request.copy(
                payPalAliasData = PayPalAliasData(paymentMethod.email)
            )
            else -> request
        }

        return retrofitRunner.executeForServerResponse {
            sampleMerchantService.createPaymentMethod(request).execute()
        }
    }

    suspend fun deletePaymentMethod(paymentMethodId: String): Result<Unit> {
        return retrofitRunner.executeWithNoResult {
            sampleMerchantService.deletePaymentMethod(paymentMethodId).execute()
        }
    }

    suspend fun getPaymentMethods(userId: String): Result<List<PaymentMethod>> {
        return retrofitRunner.executeForResponse(paymentMethodListResponseToEntity) {
            sampleMerchantService.getPaymentMethods(userId).execute()
        }
    }

    suspend fun authorizePayment(authorizePaymentRequest: AuthorizePaymentRequest): Result<AuthorizePaymentResponse> {
        return retrofitRunner.executeForServerResponse {
            sampleMerchantService.authorizePayment(UUID.randomUUID().toString(), authorizePaymentRequest).execute()
        }
    }
}