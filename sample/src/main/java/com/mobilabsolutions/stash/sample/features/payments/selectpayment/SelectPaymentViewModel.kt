/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.payments.selectpayment

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.domain.InvokeError
import com.mobilabsolutions.stash.sample.domain.InvokeFinished
import com.mobilabsolutions.stash.sample.domain.interactors.AuthorisePayment
import com.mobilabsolutions.stash.sample.domain.invoke
import com.mobilabsolutions.stash.sample.domain.launchObserve
import com.mobilabsolutions.stash.sample.domain.observers.ObservePaymentMethods
import com.mobilabsolutions.stash.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.stash.sample.shared.BaseViewModel
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SelectPaymentViewModel @AssistedInject constructor(
    @Assisted initialStateMethods: SelectPaymentViewState,
    val schedulers: AppRxSchedulers,
    observePaymentMethods: ObservePaymentMethods,
    private val authorisePayment: AuthorisePayment
) : BaseViewModel<SelectPaymentViewState>(initialStateMethods) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initialStateMethods: SelectPaymentViewState): SelectPaymentViewModel
    }

    companion object : MvRxViewModelFactory<SelectPaymentViewModel, SelectPaymentViewState> {
        override fun create(viewModelContext: ViewModelContext, state: SelectPaymentViewState): SelectPaymentViewModel? {
            val methodsFragment: SelectPaymentFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return methodsFragment.viewModelFactory.create(state)
        }
    }

    init {
        viewModelScope.launchObserve(observePaymentMethods) {
            it.execute { result ->
                val paymentMethods = result()
                if (paymentMethods != null && paymentMethods.isNotEmpty()) {
                    copy(selectedMethod = paymentMethods[0], paymentMethods = paymentMethods)
                } else {
                    copy(paymentMethods = paymentMethods.orEmpty())
                }
            }
        }
        observePaymentMethods()
    }

    fun onPaymentMethodSelected(paymentMethod: PaymentMethod) {
        setState {
            copy(selectedMethod = paymentMethod)
        }
    }

    fun onPayClicked() {
        withState { state ->
            state.selectedMethod?.let { paymentMethod ->
                val authorizePaymentRequest = AuthorizePaymentRequest(
                    amount = state.amount,
                    currency = "EUR",
                    paymentMethodId = paymentMethod.paymentMethodId,
                    reason = "Nothing"
                )
                authorisePayment(AuthorisePayment.Params(authorizePaymentRequest)).also {
                    viewModelScope.launch {
                        it.collect {
                            when (it) {
                                is InvokeError -> {
                                    errorChannel.offer(it.throwable)
                                }
                                is InvokeFinished -> {
                                    setState { copy(paymentCompleted = true) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}