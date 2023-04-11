package com.mobilabsolutions.stash.sample.domain.observers

import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.data.repositories.paymentmethod.PaymentMethodRepository
import com.mobilabsolutions.stash.sample.domain.SubjectInteractor
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-08-2019.
 */
class ObservePaymentMethods @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val paymentMethodRepository: PaymentMethodRepository
) : SubjectInteractor<Unit, List<PaymentMethod>>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override fun createObservable(params: Unit): Flow<List<PaymentMethod>> {
        return paymentMethodRepository.observePaymentMethods()
    }
}