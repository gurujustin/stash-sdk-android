/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.mobilabsolutions.stash.sample.data.entities.Cart
import com.mobilabsolutions.stash.sample.data.resultentities.CartWithProduct
import kotlinx.coroutines.flow.Flow

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Dao
abstract class CartDao : EntityDao<Cart> {
    @Transaction
    @Query("SELECT * FROM cart")
    abstract fun entriesFlow(): Flow<List<CartWithProduct>>

    @Query("SELECT * FROM cart WHERE product_id=:productId")
    abstract fun cartByProductId(productId: Long): Cart?

    @Query("DELETE FROM cart")
    abstract fun clearCart()
}