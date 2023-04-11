/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.paymentmethods

import android.app.Activity
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.core.ExtraAliasInfo
import com.mobilabsolutions.stash.core.PaymentMethodAlias
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.Stash
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.domain.InvokeError
import com.mobilabsolutions.stash.sample.domain.interactors.AddPaymentMethod
import com.mobilabsolutions.stash.sample.domain.interactors.DeletePaymentMethod
import com.mobilabsolutions.stash.sample.domain.interactors.InitialiseStash
import com.mobilabsolutions.stash.sample.domain.launchObserve
import com.mobilabsolutions.stash.sample.domain.observers.ObservePaymentMethods
import com.mobilabsolutions.stash.sample.domain.observers.ObserveUser
import com.mobilabsolutions.stash.sample.shared.BaseViewModel
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
class PaymentMethodsViewModel @AssistedInject constructor(
    @Assisted initialStateMethods: PaymentMethodsViewState,
    observeUser: ObserveUser,
    observePaymentMethods: ObservePaymentMethods,
    private val deletePaymentMethod: DeletePaymentMethod,
    private val addPaymentMethod: AddPaymentMethod,
    private val schedulers: AppRxSchedulers,
    private val initialiseStash: InitialiseStash
) : BaseViewModel<PaymentMethodsViewState>(initialStateMethods) {

    @AssistedInject.Factory
    interface Factory {
        fun create(initialStateMethods: PaymentMethodsViewState): PaymentMethodsViewModel
    }

    companion object : MvRxViewModelFactory<PaymentMethodsViewModel, PaymentMethodsViewState> {
        override fun create(viewModelContext: ViewModelContext, state: PaymentMethodsViewState): PaymentMethodsViewModel? {
            val methodsFragment: PaymentMethodsFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return methodsFragment.paymentMethodsViewModelFactory.create(state)
        }
    }

    init {
        viewModelScope.launchObserve(initialiseStash) {
            it.execute { copy(stashInitialised = it() ?: false) }
        }

        viewModelScope.launch { initialiseStash(Unit) }

        viewModelScope.launchObserve(observeUser) {
            it.execute { result -> copy(user = result()) }
        }

        viewModelScope.launchObserve(observePaymentMethods) {
            it.execute { result -> copy(paymentMethods = result().orEmpty()) }
        }

        observeUser(Unit)
        observePaymentMethods(Unit)
    }

    fun onAddBtnClicked(activity: Activity) {
        disposables += Stash.getRegistrationManager().registerPaymentMethodUsingUi(activity)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.main)
            .subscribe(this::onRegisterPaymentSuccess) { errorChannel.offer(it) }
    }

    fun onDeleteBtnClicked(paymentMethod: PaymentMethod) {
        deletePaymentMethod(DeletePaymentMethod.Params(paymentMethod)).also {
            viewModelScope.launch {
                it.collect {
                    when (it) {
                        is InvokeError -> {
                            errorChannel.offer(it.throwable)
                        }
                    }
                }
            }
        }
    }

    private fun onRegisterPaymentSuccess(paymentMethodAlias: PaymentMethodAlias) {
        val type = when (paymentMethodAlias.paymentMethodType) {
            PaymentMethodType.CC -> "CC"
            PaymentMethodType.SEPA -> "SEPA"
            PaymentMethodType.PAYPAL -> "PAY_PAL"
        }
        val paymentMethod = when (val aliasInfo = paymentMethodAlias.extraAliasInfo) {
            is ExtraAliasInfo.CreditCardExtraInfo -> PaymentMethod(_type = type,
                mask = aliasInfo.creditCardMask,
                _cardType = aliasInfo.creditCardType.name,
                expiryMonth = aliasInfo.expiryMonth.toString(),
                expiryYear = aliasInfo.expiryYear.toString()
            )
            is ExtraAliasInfo.SepaExtraInfo -> PaymentMethod(_type = type,
                iban = aliasInfo.maskedIban
            )
            is ExtraAliasInfo.PaypalExtraInfo -> PaymentMethod(_type = type,
                email = aliasInfo.email
            )
        }

        withState {
            addPaymentMethod(
                AddPaymentMethod.Params(
                    userId = it.user!!.userId,
                    aliasId = paymentMethodAlias.alias,
                    paymentMethod = paymentMethod
                )
            ).also {
                viewModelScope.launch {
                    it.collect {
                        when (it) {
                            is InvokeError -> {
                                errorChannel.offer(it.throwable)
                            }
                        }
                    }
                }
            }
        }
    }
}