/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.items

import com.airbnb.mvrx.MvRxState
import com.mobilabsolutions.stash.sample.data.entities.Product

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
data class ItemsViewState(
    val products: List<Product> = emptyList()
) : MvRxState