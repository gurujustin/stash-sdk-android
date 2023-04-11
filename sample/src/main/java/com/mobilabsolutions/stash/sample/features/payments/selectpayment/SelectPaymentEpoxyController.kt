/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.payments.selectpayment

import com.airbnb.epoxy.TypedEpoxyController
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.selectPaymentItem

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class SelectPaymentEpoxyController(
    private val callbacks: Callbacks
) : TypedEpoxyController<SelectPaymentViewState>() {

    interface Callbacks {
        fun onSelection(paymentMethod: PaymentMethod)
    }

    override fun buildModels(state: SelectPaymentViewState) {
        state.paymentMethods.forEach {
            selectPaymentItem {
                id(it.id)
                paymentMethod(it)
                isSelected((it.isSelectedPaymentMethod(state.selectedMethod)))
                selectListener { _ -> callbacks.onSelection(it) }
            }
        }
    }
}