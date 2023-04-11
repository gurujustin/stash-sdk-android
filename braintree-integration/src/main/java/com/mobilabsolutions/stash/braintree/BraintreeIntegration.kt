/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.stash.braintree.internal.uicomponents.UiComponentHandler
import com.mobilabsolutions.stash.core.CreditCardTypeWithRegex
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.exceptions.base.ConfigurationException
import com.mobilabsolutions.stash.core.exceptions.registration.RegistrationFailedException
import com.mobilabsolutions.stash.core.internal.IdempotencyKey
import com.mobilabsolutions.stash.core.internal.IntegrationInitialization
import com.mobilabsolutions.stash.core.internal.StashComponent
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasExtra
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasUpdateRequest
import com.mobilabsolutions.stash.core.internal.api.backend.v1.CreditCardConfig
import com.mobilabsolutions.stash.core.internal.api.backend.v1.PayPalConfig
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.stash.core.internal.psphandler.Integration
import com.mobilabsolutions.stash.core.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.stash.core.internal.psphandler.PayPalRegistrationRequest
import com.mobilabsolutions.stash.core.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.stash.core.model.BillingData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 *
 * Braintree integration module.
 *
 * This integration supports PayPal as a payment method. Since Braintree SDK offers only UI based
 * method registration, this integration does the same.
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BraintreeIntegration(stashComponent: StashComponent) : Integration {
    override val identifier = name

    @Inject
    lateinit var braintreeHandler: BraintreeHandler

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    @Inject
    lateinit var mobilabApi: MobilabApi

    @Inject
    lateinit var applicationContext: Context

    companion object : IntegrationCompanion {

        const val NONCE = "NONCE"

        const val DEVICE_FINGERPRINT = "DEVICE_FINGERPRINT"

        var integration: BraintreeIntegration? = null

        override val name = "BRAINTREE"

        override val supportedPaymentMethodTypes: Set<PaymentMethodType> = setOf(PaymentMethodType.CC, PaymentMethodType.PAYPAL)

        override fun create(enabledPaymentMethodTypeSet: Set<PaymentMethodType>): IntegrationInitialization {
            return object : IntegrationInitialization {

                override val enabledPaymentMethodTypes = enabledPaymentMethodTypeSet

                override fun initializedOrNull(): Integration? {
                    return integration
                }

                override fun initialize(stashComponent: StashComponent, url: String): Integration {
                    if (integration == null) {
                        integration = BraintreeIntegration(stashComponent)
                    }
                    return integration as Integration
                }
            }
        }
    }

    val braintreeIntegrationComponent: BraintreeIntegrationComponent = DaggerBraintreeIntegrationComponent.builder()
            .stashComponent(stashComponent)
            .build()

    init {
        braintreeIntegrationComponent.inject(this)
    }

    override fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        return Single.just(emptyMap())
    }

    override fun handleRegistrationRequest(
        activity: Activity,
        registrationRequest: RegistrationRequest,
        idempotencyKey: IdempotencyKey
    ): Single<String> {

        if (idempotencyKey.isUserSupplied) {
            Timber.w(applicationContext.getString(R.string.idempotency_message))
        }

        val standardizedData = registrationRequest.standardizedData
        val additionalData = registrationRequest.additionalData

        return when (standardizedData) {
            is CreditCardRegistrationRequest -> {
                val creditCardType = CreditCardTypeWithRegex.resolveCreditCardType(standardizedData.creditCardData.number)
                val data = braintreeHandler.registerCreditCard(standardizedData, additionalData)
                        .subscribeOn(Schedulers.io()).blockingGet()
                mobilabApi.updateAlias(
                        registrationRequest.standardizedData.aliasId,
                        AliasUpdateRequest(
                                extra = AliasExtra(
                                        paymentMethod = "CC",
                                        creditCardConfig = CreditCardConfig(
                                                ccExpiry = "${standardizedData.creditCardData.expiryMonth}/${standardizedData.creditCardData.expiryYear.toString().takeLast(2)}",
                                                ccMask = standardizedData.creditCardData.number.takeLast(4),
                                                ccType = creditCardType.name,
                                                ccHolderName = standardizedData.creditCardData.billingData?.fullName(),
                                                nonce = data.second,
                                                deviceData = data.third
                                        )
                                )
                        )
                ).subscribeOn(Schedulers.io()).andThen(
                        Single.just(registrationRequest.standardizedData.aliasId)
                )
            }

            is PayPalRegistrationRequest -> mobilabApi.updateAlias(
                    registrationRequest.standardizedData.aliasId,
                    AliasUpdateRequest(
                            extra = AliasExtra(
                                    payPalConfig = PayPalConfig(
                                            nonce = registrationRequest.additionalData.extraData[NONCE]
                                                    ?: error("Missing nonce"),
                                            deviceData = registrationRequest.additionalData.extraData[DEVICE_FINGERPRINT]
                                                    ?: error("Missing device fingerprint")
                                    ),
                                    paymentMethod = "PAY_PAL",
                                    personalData = BillingData(email = registrationRequest.additionalData.extraData[BillingData.ADDITIONAL_DATA_EMAIL])
                            )
                    )
            ).subscribeOn(Schedulers.io()).andThen(
                    Single.just(registrationRequest.standardizedData.aliasId)
            )
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
            PaymentMethodType.CC -> uiComponentHandler.handleCreditCardDataEntryRequest(activity, additionalRegistrationData, resultObservable)

            PaymentMethodType.PAYPAL -> braintreeHandler.tokenizePaymentMethods(activity, additionalRegistrationData)
                    .flatMapObservable {
                        Observable.just(
                                AdditionalRegistrationData(
                                        mapOf(
                                                BillingData.ADDITIONAL_DATA_EMAIL to it.first,
                                                NONCE to it.second,
                                                DEVICE_FINGERPRINT to it.third
                                        )
                                )
                        )
                    }

            PaymentMethodType.SEPA -> throw ConfigurationException("SEPA is not supported in Braintree integration")
        }
    }
}
