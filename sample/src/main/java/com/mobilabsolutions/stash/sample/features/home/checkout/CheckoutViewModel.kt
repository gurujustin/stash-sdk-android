/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.checkout

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.sample.data.resultentities.CartWithProduct
import com.mobilabsolutions.stash.sample.domain.interactors.ChangeCartQuantity
import com.mobilabsolutions.stash.sample.domain.interactors.CompletePayment
import com.mobilabsolutions.stash.sample.domain.invoke
import com.mobilabsolutions.stash.sample.domain.launchObserve
import com.mobilabsolutions.stash.sample.domain.observers.ObserveCarts
import com.mobilabsolutions.stash.sample.domain.observers.ObservePaymentCompleted
import com.mobilabsolutions.stash.sample.shared.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class CheckoutViewModel @AssistedInject constructor(
    @Assisted initialState: CheckoutViewState,
    observeCarts: ObserveCarts,
    observePaymentCompleted: ObservePaymentCompleted,
    private val changeCartQuantity: ChangeCartQuantity,
    private val completePayment: CompletePayment
) : BaseViewModel<CheckoutViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: CheckoutViewState): CheckoutViewModel
    }

    companion object : MvRxViewModelFactory<CheckoutViewModel, CheckoutViewState> {
        override fun create(viewModelContext: ViewModelContext, state: CheckoutViewState): CheckoutViewModel? {
            val fragment: CheckoutFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.checkoutViewModelFactory.create(state)
        }
    }

    init {
        viewModelScope.launchObserve(observeCarts) { flow ->
            flow.distinctUntilChanged().execute {
                val cartItems = it().orEmpty()
                val totalPrice = cartItems.map { it.entry!!.quantity * it.product.price }
                    .sumBy { it }
                copy(
                    cartItems = cartItems,
                    totalAmount = totalPrice,
                    showEmptyView = cartItems.isEmpty()
                )
            }
        }
        observeCarts()
        viewModelScope.launchObserve(observePaymentCompleted) {
            it.distinctUntilChanged().execute {
                copy(paymentCompleted = it() ?: false)
            }
        }
        observePaymentCompleted()
    }

    fun onAddButtonClicked(cartWithProduct: CartWithProduct) {
        changeCartQuantity(ChangeCartQuantity.Params(true, cartWithProduct))
    }

    fun onRemoveButtonClicked(cartWithProduct: CartWithProduct) {
        changeCartQuantity(ChangeCartQuantity.Params(false, cartWithProduct))
    }

    fun closeSnackBar() {
        completePayment()
    }
}