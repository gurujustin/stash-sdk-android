/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal

import android.app.Activity
import com.mobilabsolutions.stash.core.PaymentMethodAlias
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.RegistrationManager
import com.mobilabsolutions.stash.core.model.CreditCardData
import com.mobilabsolutions.stash.core.model.SepaData
import io.reactivex.Single
import java.util.UUID
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class RegistrationManagerImpl @Inject internal constructor(
    private val pspCoordinator: PspCoordinator

) : RegistrationManager {

    override fun registerCreditCard(
        activity: Activity,
        creditCardData: CreditCardData,
        idempotencyKey: UUID?
    ): Single<PaymentMethodAlias> {
        return pspCoordinator.handleRegisterCreditCard(
                activity = activity,
                creditCardData = creditCardData,
                idempotencyKey = IdempotencyKey(idempotencyKey)
        )
    }

    override fun registerSepaAccount(
        activity: Activity,
        sepaData: SepaData,
        idempotencyKey: UUID?
    ): Single<PaymentMethodAlias> {
        return pspCoordinator.handleRegisterSepa(
                activity = activity,
                sepaData = sepaData,
                idempotencyKey = IdempotencyKey(idempotencyKey)
        )
    }

    override fun getAvailablePaymentMethodsTypes(): Set<PaymentMethodType> {
        return pspCoordinator.getAvailablePaymentMethods()
    }

    override fun registerPaymentMethodUsingUi(activity: Activity, specificPaymentMethodType: PaymentMethodType?): Single<PaymentMethodAlias> {
        return pspCoordinator.handleRegisterPaymentMethodUsingUi(activity, specificPaymentMethodType)
    }
}

data class IdempotencyKey(
    val key: String,
    val isUserSupplied: Boolean
) {
    constructor(key: UUID?) : this(
            (key ?: UUID.randomUUID()).toString(),
            key != null
    )
}