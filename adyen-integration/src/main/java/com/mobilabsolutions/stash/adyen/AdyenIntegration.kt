/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.adyen

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.stash.adyen.internal.uicomponents.UiComponentHandler
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.exceptions.base.ConfigurationException
import com.mobilabsolutions.stash.core.exceptions.registration.RegistrationFailedException
import com.mobilabsolutions.stash.core.internal.IdempotencyKey
import com.mobilabsolutions.stash.core.internal.IntegrationInitialization
import com.mobilabsolutions.stash.core.internal.StashComponent
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.stash.core.internal.psphandler.Integration
import com.mobilabsolutions.stash.core.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.stash.core.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.stash.core.internal.psphandler.SepaRegistrationRequest
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class AdyenIntegration @Inject constructor(
    stashComponent: StashComponent
) : Integration {
    override val identifier = name

    @Inject
    lateinit var adyenHandler: AdyenHandler

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    companion object : IntegrationCompanion {

        var integration: AdyenIntegration? = null

        override val name = "ADYEN"

        override val supportedPaymentMethodTypes: Set<PaymentMethodType> = setOf(PaymentMethodType.CC, PaymentMethodType.SEPA)

        override fun create(enabledPaymentMethodTypeSet: Set<PaymentMethodType>): IntegrationInitialization {
            return object : IntegrationInitialization {
                override val enabledPaymentMethodTypes = enabledPaymentMethodTypeSet

                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(stashComponent: StashComponent, url: String): Integration {
                    if (integration == null) {
                        if (url.isEmpty()) {
                            integration = AdyenIntegration(stashComponent)
                        } else {
                            throw RuntimeException("Adyen doesn't support custom endpoint url")
                        }
                    }
                    return integration as Integration
                }
            }
        }
    }

    val adyenIntegrationComponent: AdyenIntegrationComponent = DaggerAdyenIntegrationComponent.builder()
            .stashComponent(stashComponent)
            .build()

    init {
        adyenIntegrationComponent.inject(this)
    }

    override fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        return adyenHandler.getPreparationData(method)
    }

    override fun handleRegistrationRequest(
        activity: Activity,
        registrationRequest: RegistrationRequest,
        idempotencyKey: IdempotencyKey
    ): Single<String> {
        val standardizedData = registrationRequest.standardizedData
        val additionalData = registrationRequest.additionalData

        return when (standardizedData) {
            is CreditCardRegistrationRequest -> {
                if (idempotencyKey.isUserSupplied) {
//                    Timber.w(applicationContext.getString(R.string.idempotency_message))
                }
                adyenHandler.registerCreditCard(activity, standardizedData, additionalData)
            }
            is SepaRegistrationRequest -> adyenHandler.registerSepa(standardizedData)
            else -> throw RegistrationFailedException("Unsupported payment method")
        }
    }

    override fun handlePaymentMethodEntryRequest(
        activity: AppCompatActivity,
        paymentMethodType: PaymentMethodType,
        additionalRegistrationData: AdditionalRegistrationData,
        resultObservable: Observable<UiRequestHandler.DataEntryResult>
    ): Observable<AdditionalRegistrationData> {
        return when (paymentMethodType) {
            PaymentMethodType.CC -> uiComponentHandler.handleCreditCardDataEntryRequest(activity, resultObservable)
            PaymentMethodType.SEPA -> uiComponentHandler.handleSepaDataEntryRequest(activity, resultObservable)
            PaymentMethodType.PAYPAL -> throw ConfigurationException("PayPal is not supported in Adyen integration")
        }
    }
}
