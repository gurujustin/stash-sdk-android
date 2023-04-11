/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.psphandler

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.internal.IdempotencyKey
import com.mobilabsolutions.stash.core.internal.IntegrationInitialization
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import io.reactivex.Observable
import io.reactivex.Single

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
typealias PspIdentifier = String

interface Integration {
    val identifier: PspIdentifier

    fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>>

    fun handleRegistrationRequest(
        activity: Activity,
        registrationRequest: RegistrationRequest,
        idempotencyKey: IdempotencyKey
    ): Single<String>

    fun handlePaymentMethodEntryRequest(
        activity: AppCompatActivity,
        paymentMethodType: PaymentMethodType,
        additionalRegistrationData: AdditionalRegistrationData,
        resultObservable: Observable<UiRequestHandler.DataEntryResult>
    ): Observable<AdditionalRegistrationData>
}

interface IntegrationCompanion {

    val name: PspIdentifier

    val supportedPaymentMethodTypes: Set<PaymentMethodType>

    fun create(enabledPaymentMethodTypeSet: Set<PaymentMethodType>): IntegrationInitialization
}
