package com.mobilabsolutions.stash.sample.domain.observers

import com.mobilabsolutions.stash.sample.data.repositories.paymentmethod.PaymentMethodRepository
import com.mobilabsolutions.stash.sample.domain.SubjectInteractor
import com.mobilabsolutions.stash.sample.inject.ProcessLifetime
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-08-2019.
 */
class ObservePaymentCompleted @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    @ProcessLifetime val processScope: CoroutineScope,
    private val paymentMethodRepository: PaymentMethodRepository
) : SubjectInteractor<Unit, Boolean>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override fun createObservable(params: Unit): Flow<Boolean> {
        return paymentMethodRepository.observePaymentCompleted()
    }
}