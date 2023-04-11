package com.mobilabsolutions.stash.sample.domain.observers

import com.mobilabsolutions.stash.sample.data.repositories.cart.CartRepository
import com.mobilabsolutions.stash.sample.data.resultentities.CartWithProduct
import com.mobilabsolutions.stash.sample.domain.SubjectInteractor
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-08-2019.
 */
class ObserveCarts @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val cartRepository: CartRepository
) : SubjectInteractor<Unit, List<CartWithProduct>>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override fun createObservable(params: Unit): Flow<List<CartWithProduct>> {
        return cartRepository.observeCartsFlow()
    }
}