/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.paymentmethod

import com.mobilabsolutions.stash.sample.data.DatabaseTransactionRunner
import com.mobilabsolutions.stash.sample.data.daos.EntityInserter
import com.mobilabsolutions.stash.sample.data.daos.PaymentMethodDao
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class LocalPaymentMethodStore @Inject constructor(
    private val transactionRunner: DatabaseTransactionRunner,
    private val entityInserter: EntityInserter,
    private val paymentMethodDao: PaymentMethodDao
) {
    fun observePaymentMethods() = paymentMethodDao.entriesObservable()

    suspend fun deletePaymentMethod(paymentMethod: PaymentMethod) = transactionRunner {
        paymentMethodDao.delete(paymentMethod)
    }

    suspend fun savePaymentMethod(paymentMethod: PaymentMethod, paymentMethodId: String) = transactionRunner {
        val id = paymentMethodDao.entityByPaymentMethodId(paymentMethodId)?.id ?: 0L
        entityInserter.insertOrUpdate(paymentMethodDao, paymentMethod.copy(id = id, paymentMethodId = paymentMethodId))
    }

    suspend fun savePaymentMethodList(userId: String, paymentMethodList: List<PaymentMethod>) = transactionRunner {
        paymentMethodList.forEach {
            val id = paymentMethodDao.entityByPaymentMethodId(it.paymentMethodId)?.id ?: 0L
            entityInserter.insertOrUpdate(paymentMethodDao, it.copy(id = id, userId = userId))
        }
    }
}