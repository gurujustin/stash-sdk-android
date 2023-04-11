/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.stash.bspayone.internal.BsPayoneRegistrationRequest
import com.mobilabsolutions.stash.bspayone.internal.uicomponents.UiComponentHandler
import com.mobilabsolutions.stash.core.PaymentMethodType
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
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneIntegration private constructor(
    stashComponent: StashComponent,
    url: String = BuildConfig.newBsApiUrl
) : Integration {
    override val identifier = name

    @Inject
    lateinit var bsPayoneHandler: BsPayoneHandler

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    @Inject
    lateinit var applicationContext: Context

    companion object : IntegrationCompanion {

        var integration: BsPayoneIntegration? = null

        override val name = "BS_PAYONE"

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
                            integration = BsPayoneIntegration(stashComponent)
                        } else {
                            integration = BsPayoneIntegration(stashComponent, url)
                        }
                    }
                    return integration as Integration
                }
            }
        }
    }

    val bsPayoneIntegrationComponent: BsPayoneIntegrationComponent

    init {
        bsPayoneIntegrationComponent = DaggerBsPayoneIntegrationComponent.builder()
                .stashComponent(stashComponent)
                .bsPayoneModule(BsPayoneModule(url))
                .build()

        bsPayoneIntegrationComponent.inject(this)
    }

    override fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        return Single.just(emptyMap())
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
                    Timber.w(applicationContext.getString(R.string.idempotency_message))
                }
                bsPayoneHandler.registerCreditCard(
                        standardizedData,
                        BsPayoneRegistrationRequest.fromMap(additionalData.extraData)
                )
            }
            is SepaRegistrationRequest -> {
                bsPayoneHandler.registerSepa(
                        standardizedData
                )
            }
            else -> {
                throw RuntimeException("Unsupported payment method")
            }
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
            PaymentMethodType.PAYPAL -> throw RuntimeException("PayPal is not supported in BsPayone integration")
        }
    }
}
