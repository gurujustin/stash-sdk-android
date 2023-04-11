/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal

import android.app.Activity
import com.mobilabsolutions.stash.core.CreditCardTypeWithRegex
import com.mobilabsolutions.stash.core.ExtraAliasInfo
import com.mobilabsolutions.stash.core.PaymentMethodAlias
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.exceptions.ExceptionMapper
import com.mobilabsolutions.stash.core.exceptions.base.BasePaymentException
import com.mobilabsolutions.stash.core.exceptions.registration.UnknownException
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.stash.core.internal.psphandler.Integration
import com.mobilabsolutions.stash.core.internal.psphandler.PayPalRegistrationRequest
import com.mobilabsolutions.stash.core.internal.psphandler.RegistrationRequest
import com.mobilabsolutions.stash.core.internal.psphandler.SepaRegistrationRequest
import com.mobilabsolutions.stash.core.internal.uicomponents.RegistrationProcessHostActivity
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.stash.core.model.BillingData
import com.mobilabsolutions.stash.core.model.CreditCardData
import com.mobilabsolutions.stash.core.model.SepaData
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.util.Random
import java.util.UUID
import javax.inject.Inject

/**
 * PspCoordinator class is responsible for coordinating the initial part of the alias registration flow.
 *
 * ### Responsibilities
 *
 * Coordinator is responsible for:
 * * Routing UI registration requests to [UiRequestHandler]
 * * Resolving the data provided in [AdditionalRegistrationData] so that:
 *     * A successful alias creation request can be executed
 *     * A well formed RegistrationRequest can be delivered to the specific PSP integration
 * * Mapping backend exceptions into SDK exceptions
 * * Constructing a [PaymentMethodAlias] that is returned to 3rd party developer
 *
 * ### Processing [AdditionalRegistrationData]
 *
 * Since various PSPs have various registration requirements, apart from standardized data,
 * additional data might be required. This additional data is provided either directly by 3rd party
 * developer, or by UI component defined in appropriate PSP Integration module.
 *
 * In essence AdditionalRegistrationData is a wrapper around a map of string, PspCoordinator
 * knows which of the keys in that map are releveant for initial alias creation and uses them accordingly
 * while the rest of the values are passed on to the PSP Integration to be used when communicating
 * with the PSP
 *
 * ### UI Registration request handling
 *
 * PSP Coordinator determines if the UI registration request requires if a UI payment method type picker
 * should be show or not, and if not which screen should be requested. This is then handled by UiRequestHandler
 * which handles the UI flow.
 *
 * ### Idempotency
 *
 * PSP Coordinator hands over the idempotency-key to the PSP Implementation. Supports for idempotency depends on each PSP
 *
 * When handling UI requests, there are only two possible outcomes:
 *  * Success
 *  * UserCancelledException
 *  As user is forced to either enter valid input until success is achieved, or cancell entering the
 *  data altogether
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
internal class PspCoordinator @Inject constructor(
    private val mobilabApi: MobilabApi,
    private val exceptionMapper: ExceptionMapper,
    private val integrations: Map<@JvmSuppressWildcards Integration, @JvmSuppressWildcards Set<@JvmSuppressWildcards PaymentMethodType>>,
    private val uiRequestHandler: UiRequestHandler
) {

    /**
     * This helper handler method is used directly from the API, it resolves the
     * proper PSP integration (as there can only be 1-1 mapping between PSP Integration and
     * payment method type (in this case CreditCard) and forwards it to [handleRegisterCreditCard]
     */
    fun handleRegisterCreditCard(
        activity: Activity,
        creditCardData: CreditCardData,
        additionalUIData: AdditionalRegistrationData = AdditionalRegistrationData(),
        idempotencyKey: IdempotencyKey
    ): Single<PaymentMethodAlias> {
        return handleRegisterCreditCard(
            activity,
            creditCardData,
            additionalUIData,
            integrations
                .filter { it.value.contains(PaymentMethodType.CC) }
                .keys
                .first(),
            idempotencyKey
        )
    }

    /**
     * Handler method that creates the alias by calling the backend endpoint, processes additional data, and upon PSP Integration flow
     * completion creates the PaymentMethodAlias object that is returned to 3rd party developer.
     */
    private fun handleRegisterCreditCard(
        activity: Activity,
        creditCardData: CreditCardData,
        additionalUIData: AdditionalRegistrationData,
        chosenIntegration: Integration,
        idempotencyKey: IdempotencyKey
    ): Single<PaymentMethodAlias> {
        val billingData = creditCardData.billingData ?: BillingData()
        additionalUIData.extraData[BillingData.ADDITIONAL_DATA_COUNTRY]?.let { billingData.country = it }
        additionalUIData.extraData[BillingData.ADDITIONAL_DATA_FIRST_NAME]?.let { billingData.firstName = it }
        additionalUIData.extraData[BillingData.ADDITIONAL_DATA_LAST_NAME]?.let { billingData.lastName = it }

        // TODO Validate in case data is being sent from custom UI before starting the communication with the backend

        return chosenIntegration.getPreparationData(PaymentMethodType.CC).flatMap { preparationData ->
            mobilabApi.createAlias(chosenIntegration.identifier, idempotencyKey.key)
                .subscribeOn(Schedulers.io())
                .flatMap {
                    val standardizedData = CreditCardRegistrationRequest(creditCardData = creditCardData, billingData = billingData, aliasId = it.aliasId)
                    val additionalData = AdditionalRegistrationData(it.pspExtra + additionalUIData.extraData)
                    val registrationRequest = RegistrationRequest(standardizedData, additionalData)

                    val pspAliasSingle = chosenIntegration.handleRegistrationRequest(activity, registrationRequest, idempotencyKey)

                    pspAliasSingle.map { alias ->
                        val cardType = CreditCardTypeWithRegex.resolveCreditCardType(standardizedData.creditCardData.number)
                        val lastDigits = standardizedData.creditCardData.number.takeLast(4)
                        val creditCardExtraInfo = ExtraAliasInfo.CreditCardExtraInfo(
                            creditCardType = cardType,
                            creditCardMask = lastDigits,
                            expiryMonth = standardizedData.creditCardData.expiryMonth,
                            expiryYear = standardizedData.creditCardData.expiryYear
                        )
                        PaymentMethodAlias(alias, PaymentMethodType.CC, extraAliasInfo = creditCardExtraInfo)
                    }
                }.processErrors()
        }
    }

    /**
     * Helper handler method is used directly from the API, it resolves the
     * proper PSP integration (as there can only be 1-1 mapping between PSP Integration and
     * payment method type (in this case Sepa) and forwards it to [handleRegisterSepa]
     */
    fun handleRegisterSepa(
        activity: Activity,
        sepaData: SepaData,
        additionalUIData: AdditionalRegistrationData = AdditionalRegistrationData(),
        idempotencyKey: IdempotencyKey
    ): Single<PaymentMethodAlias> {

        return handleRegisterSepa(
            activity,
            sepaData,
            additionalUIData,
            integrations
                .filter { it.value.contains(PaymentMethodType.SEPA) }
                .keys
                .first(),
            idempotencyKey)
    }

    /**
     * Handler method that creates the alias by calling the backend endpoint, processes additional data, and upon PSP Integration flow
     * completion creates the PaymentMethodAlias object that is returned to 3rd party developer.
     */
    fun handleRegisterSepa(
        activity: Activity,
        sepaData: SepaData,
        additionalUIData: AdditionalRegistrationData,
        chosenIntegration: Integration,
        idempotencyKey: IdempotencyKey
    ): Single<PaymentMethodAlias> {
        val billingData = sepaData.billingData ?: BillingData()
        additionalUIData.extraData[BillingData.ADDITIONAL_DATA_COUNTRY]?.let { billingData.country = it }
        additionalUIData.extraData[BillingData.ADDITIONAL_DATA_FIRST_NAME]?.let { billingData.firstName = it }
        additionalUIData.extraData[BillingData.ADDITIONAL_DATA_LAST_NAME]?.let { billingData.lastName = it }

        // TODO Validate in case data is being sent from custom UI before starting the communication with the backend
        return chosenIntegration.getPreparationData(PaymentMethodType.SEPA).flatMap { preparationData ->
            mobilabApi.createAlias(chosenIntegration.identifier, idempotencyKey.key, preparationData)
                .subscribeOn(Schedulers.io())
                .flatMap { aliasResponse ->

                    val standardizedData = SepaRegistrationRequest(sepaData = sepaData, billingData = billingData, aliasId = aliasResponse.aliasId)
                    val additionalData = AdditionalRegistrationData(aliasResponse.pspExtra + additionalUIData.extraData)
                    val registrationRequest = RegistrationRequest(standardizedData, additionalData)

                    chosenIntegration.handleRegistrationRequest(activity, registrationRequest, idempotencyKey)
                        .map {
                            val maskedIban = standardizedData.sepaData.iban
                            PaymentMethodAlias(it, PaymentMethodType.SEPA, extraAliasInfo = ExtraAliasInfo.SepaExtraInfo(maskedIban))
                        }
                }
        }.processErrors()
    }

    /**
     * Returns a set of supported payment methods. This is dependant on configuration of the SDK,
     * as well as which payment method are supported by which integration.
     */
    fun getAvailablePaymentMethods(): Set<PaymentMethodType> {
        return integrations.values.flatten().toSet()
    }

    /**
     * A handler method that checks if the chooser needs to be shown, and
     * which screen should be shown, based on available integrations and SDK configuration
     */
    fun handleRegisterPaymentMethodUsingUi(activity: Activity, specificPaymentMethodType: PaymentMethodType?): Single<PaymentMethodAlias> {
        val requestId = Random().nextInt(Int.MAX_VALUE)
        val resolvedPaymentMethodType = if (specificPaymentMethodType != null) {
            Single.just(specificPaymentMethodType)
        } else {
            uiRequestHandler.askUserToChosePaymentMethod(activity, requestId)
        }
        return resolvedPaymentMethodType.flatMap {
            when (it) {
                PaymentMethodType.PAYPAL -> registerPayPalUsingUIComponent(activity, requestId)
                PaymentMethodType.CC -> uiRequestHandler.registerCreditCardUsingUIComponent(activity, this, requestId)
                PaymentMethodType.SEPA -> uiRequestHandler.registerSepaUsingUIComponent(activity, this, requestId)
            }
        }.onErrorResumeNext {
            if (it is UiRequestHandler.EntryCancelled) {
                handleRegisterPaymentMethodUsingUi(activity, specificPaymentMethodType)
            } else {
                uiRequestHandler.closeFlow()
                Single.error(it)
            }
        }
    }

    /**
     * PayPal is different from other payment methods like SEPA and Credit Card, as it only allows registration using
     * flow external to the SDK. For that reason we don't offer a possibility of creating a specific 3rd party
     * UI, but just a request to register using pre-build components.
     *
     * This method will similarly to SEPA and Credit Card create the alias by calling the backend endpoint, processes additional data, and upon PayPal flow
     * completion create the PaymentMethodAlias object that is returned to 3rd party developer.
     */
    private fun registerPayPalUsingUIComponent(activity: Activity, requestId: Int): Single<PaymentMethodAlias> {
        val hostActivity = uiRequestHandler.hostActivityProvider.value
        if (hostActivity != null && hostActivity is RegistrationProcessHostActivity) {
            hostActivity.showPaypalLoading()
        }
        val chosenIntegration = integrations.filter {
            it.value.contains(PaymentMethodType.PAYPAL)
        }.keys.first()

        var email = ""
        val idempotencyKey = IdempotencyKey(UUID.randomUUID().toString(), false)
        return chosenIntegration.getPreparationData(PaymentMethodType.PAYPAL).flatMap { preparationData ->
            mobilabApi.createAlias(chosenIntegration.identifier, idempotencyKey.key, preparationData)
                .subscribeOn(Schedulers.io())
                .flatMap { aliasResponse ->
                    uiRequestHandler.handlePaypalMethodEntryRequest(
                        activity,
                        chosenIntegration,
                        AdditionalRegistrationData(aliasResponse.pspExtra),
                        requestId)
                        .flatMap {
                            email = it.extraData[BillingData.ADDITIONAL_DATA_EMAIL] ?: ""
                            val additionalData = it
                            val standardizedData = PayPalRegistrationRequest(aliasResponse.aliasId)
                            val registrationRequest = RegistrationRequest(standardizedData, additionalData)
                            chosenIntegration.handleRegistrationRequest(activity, registrationRequest, idempotencyKey)
                        }
                }.map {
                    PaymentMethodAlias(it, PaymentMethodType.PAYPAL, extraAliasInfo = ExtraAliasInfo.PaypalExtraInfo(email = email))
                }
        }
    }

    private fun <T> Single<T>.processErrors(): Single<T> {
        return onErrorResumeNext {
            when (it) {
                is HttpException -> Single.error(exceptionMapper.mapError(it))
                is BasePaymentException -> Single.error(it)
                else -> Single.error(UnknownException("${it.message}"))
            }
        }
    }
}
