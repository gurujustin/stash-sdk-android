/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.paymentmethod

import com.mobilabsolutions.stash.sample.data.entities.ErrorResult
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.data.entities.Success
import com.mobilabsolutions.stash.sample.data.repositories.cart.LocalCartStore
import com.mobilabsolutions.stash.sample.extensions.launchOrJoin
import com.mobilabsolutions.stash.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Singleton
class PaymentMethodRepositoryImpl @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val localPaymentMethodStore: LocalPaymentMethodStore,
    private val localCartStore: LocalCartStore,
    private val remotePaymentMethodDataSource: RemotePaymentMethodDataSource
) : PaymentMethodRepository {
    private val _paymentCompletedChannel = ConflatedBroadcastChannel<Boolean>()

    override fun observePaymentCompleted(): Flow<Boolean> {
        return _paymentCompletedChannel.asFlow()
    }

    override suspend fun completePayment() {
        _paymentCompletedChannel.offer(false)
    }

    override fun observePaymentMethods() = localPaymentMethodStore.observePaymentMethods()

    override suspend fun updatePaymentMethods(userId: String) = supervisorScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.getPaymentMethods(userId) }
        when (val result = remoteJob.await()) {
            is Success -> localPaymentMethodStore.savePaymentMethodList(userId, result.data)
            is ErrorResult -> throw result.exception
        }
        Unit
    }

    override suspend fun addPaymentMethod(userId: String, aliasId: String, paymentMethod: PaymentMethod) = supervisorScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.addPaymentMethod(userId, aliasId, paymentMethod) }
        when (val result = remoteJob.await()) {
            is Success -> localPaymentMethodStore.savePaymentMethod(paymentMethod.copy(userId = userId), result.data.paymentMethodId)
            is ErrorResult -> throw result.exception
        }
        Unit
    }

    override suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) = supervisorScope {
        val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.deletePaymentMethod(paymentMethod.paymentMethodId) }
        when (val result = remoteJob.await()) {
            is Success -> localPaymentMethodStore.deletePaymentMethod(paymentMethod)
            is ErrorResult -> throw result.exception
        }
        Unit
    }

    override suspend fun authorizePayment(authorizePaymentRequest: AuthorizePaymentRequest) {
        launchOrJoin("authorize_payment_${authorizePaymentRequest.paymentMethodId}") {
            val remoteJob = async(dispatchers.io) { remotePaymentMethodDataSource.authorizePayment(authorizePaymentRequest) }
            when (val result = remoteJob.await()) {
                is Success -> {
                    localCartStore.emptyCart()
                    _paymentCompletedChannel.offer(true)
                }
                is ErrorResult -> throw result.exception
            }
        }
    }
}