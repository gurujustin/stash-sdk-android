package com.mobilabsolutions.stash.sample.domain.observers

import com.mobilabsolutions.stash.sample.data.entities.Product
import com.mobilabsolutions.stash.sample.data.repositories.product.ProductRepository
import com.mobilabsolutions.stash.sample.domain.SubjectInteractor
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-08-2019.
 */
class ObserveProducts @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val productRepository: ProductRepository
) : SubjectInteractor<Unit, List<Product>>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override fun createObservable(params: Unit): Flow<List<Product>> {
        return productRepository.observerProducts()
    }
}