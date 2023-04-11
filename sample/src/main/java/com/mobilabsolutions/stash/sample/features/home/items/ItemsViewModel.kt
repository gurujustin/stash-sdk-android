/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.items

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.sample.data.entities.Product
import com.mobilabsolutions.stash.sample.domain.interactors.AddProductToCart
import com.mobilabsolutions.stash.sample.domain.launchObserve
import com.mobilabsolutions.stash.sample.domain.observers.ObserveProducts
import com.mobilabsolutions.stash.sample.shared.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class ItemsViewModel @AssistedInject constructor(
    @Assisted initialState: ItemsViewState,
    observeProducts: ObserveProducts,
    private val addProductToCart: AddProductToCart
) : BaseViewModel<ItemsViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: ItemsViewState): ItemsViewModel
    }

    companion object : MvRxViewModelFactory<ItemsViewModel, ItemsViewState> {
        override fun create(viewModelContext: ViewModelContext, state: ItemsViewState): ItemsViewModel? {
            val fragment: ItemsFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.itemsViewModelFactory.create(state)
        }
    }

    init {
        viewModelScope.launchObserve(observeProducts) {
            it.execute { result -> copy(products = result().orEmpty()) }
        }
        observeProducts(Unit)
    }

    fun onProductClicked(product: Product) {
        addProductToCart(AddProductToCart.Params(productId = product.id))
    }
}