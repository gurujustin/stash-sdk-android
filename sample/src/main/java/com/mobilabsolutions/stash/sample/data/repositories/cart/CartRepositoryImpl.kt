/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.cart

import com.mobilabsolutions.stash.sample.data.resultentities.CartWithProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Singleton
class CartRepositoryImpl @Inject constructor(
    private val localCartStore: LocalCartStore
) : CartRepository {
    override fun observeCartsFlow(): Flow<List<CartWithProduct>> = localCartStore.observerCartsFlow()

    override suspend fun changeCartQuantity(add: Boolean, cartWithProduct: CartWithProduct) {
        if (add) {
            addCartQuantity(cartWithProduct)
        } else {
            removeCart(cartWithProduct)
        }
    }

    override suspend fun addProductToCart(productId: Long) {
        localCartStore.addProductToCart(productId)
    }

    private suspend fun addCartQuantity(cartWithProduct: CartWithProduct) {
        localCartStore.addCartQuantity(cartWithProduct)
    }

    private suspend fun removeCart(cartWithProduct: CartWithProduct) {
        localCartStore.removeCart(cartWithProduct)
    }
}