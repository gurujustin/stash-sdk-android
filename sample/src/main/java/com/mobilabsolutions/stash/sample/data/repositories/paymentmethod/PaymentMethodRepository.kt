/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.paymentmethod

import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.network.request.AuthorizePaymentRequest
import kotlinx.coroutines.flow.Flow

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface PaymentMethodRepository {
    fun observePaymentMethods(): Flow<List<PaymentMethod>>
    fun observePaymentCompleted(): Flow<Boolean>
    suspend fun updatePaymentMethods(userId: String)
    suspend fun deletePaymentMethod(paymentMethod: PaymentMethod)
    suspend fun addPaymentMethod(userId: String, aliasId: String, paymentMethod: PaymentMethod)
    suspend fun authorizePayment(authorizePaymentRequest: AuthorizePaymentRequest)
    suspend fun completePayment()
}