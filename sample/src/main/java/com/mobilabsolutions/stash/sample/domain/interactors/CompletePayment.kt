package com.mobilabsolutions.stash.sample.domain.interactors

import com.mobilabsolutions.stash.sample.data.repositories.paymentmethod.PaymentMethodRepository
import com.mobilabsolutions.stash.sample.domain.Interactor
import com.mobilabsolutions.stash.sample.inject.ProcessLifetime
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-08-2019.
 */
class CompletePayment @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    @ProcessLifetime val processScope: CoroutineScope,
    private val paymentMethodRepository: PaymentMethodRepository
) : Interactor<Unit>() {
    override val scope: CoroutineScope = processScope + dispatchers.io

    override suspend fun doWork(params: Unit) {
        paymentMethodRepository.completePayment()
    }
}