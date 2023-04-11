package com.mobilabsolutions.stash.sample.features.home.items

import com.airbnb.epoxy.TypedEpoxyController
import com.mobilabsolutions.stash.sample.data.entities.Product
import com.mobilabsolutions.stash.sample.productItem

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 21-08-2019.
 */
class ItemsEpoxyController(
    private val callbacks: Callbacks
) : TypedEpoxyController<ItemsViewState>() {
    interface Callbacks {
        fun onProductClicked(product: Product)
    }

    override fun buildModels(state: ItemsViewState) {
        val products = state.products
        products.forEach {
            productItem {
                id(it.id)
                product(it)
                clickListener { _ -> callbacks.onProductClicked(it) }
            }
        }
    }
}