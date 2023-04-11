/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.product

import com.mobilabsolutions.stash.sample.data.entities.Product
import kotlinx.coroutines.flow.Flow

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
interface ProductRepository {
    fun observerProducts(): Flow<List<Product>>
}