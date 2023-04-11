/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.adyen

import android.annotation.SuppressLint
import android.app.Activity
import com.adyen.checkout.base.ActionComponentData
import com.adyen.checkout.base.model.payments.response.Threeds2FingerprintAction
import com.adyen.checkout.core.exeption.CheckoutException
import com.adyen.checkout.cse.Card
import com.adyen.checkout.cse.internal.CardEncryptorImpl
import com.google.gson.Gson
import com.mobilabsolutions.stash.adyen.model.AdyenThreeDsResult
import com.mobilabsolutions.stash.core.CreditCardTypeWithRegex
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.exceptions.base.OtherException
import com.mobilabsolutions.stash.core.internal.IntegrationScope
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyChallengeRequestDto
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyRedirectDto
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyThreeDsDto
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyThreeDsRequestDto
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasExtra
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasUpdateRequest
import com.mobilabsolutions.stash.core.internal.api.backend.v1.CreditCardConfig
import com.mobilabsolutions.stash.core.internal.api.backend.v1.SepaConfig
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.stash.core.internal.psphandler.SepaRegistrationRequest
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
class AdyenHandler @Inject constructor(
    private val mobilabApi: MobilabApi
) {
    private var threeDsCompletedSubject: Subject<Unit> = PublishSubject.create()
    private var threeDsErrorSubject: Subject<CheckoutException> = PublishSubject.create()
    private val clientEncryptionKey = "clientEncryptionKey"
    private val gson = Gson()

    fun registerCreditCard(
        activity: Activity,
        creditCardRegistrationRequest: CreditCardRegistrationRequest,
        additionalData: AdditionalRegistrationData
    ): Single<String> {
        return Single.create {
            val creditCardData = creditCardRegistrationRequest.creditCardData
            val publicKey = additionalData.extraData[clientEncryptionKey]
                ?: error("No client encrypt key!")

            val card = Card.Builder()
                .setNumber(creditCardData.number)
                .setExpiryDate(creditCardData.expiryMonth, creditCardData.expiryYear)
                .setSecurityCode(creditCardData.cvv)
                .build()

            val cardEncryptor = CardEncryptorImpl()
            val encryptCard = cardEncryptor.encryptFields(card, publicKey)

            val creditCardType = CreditCardTypeWithRegex.resolveCreditCardType(creditCardData.number)
            val creditCardTypeName = creditCardType.name

            val exchangeAlias = mobilabApi.exchangeAlias(
                creditCardRegistrationRequest.aliasId,
                AliasUpdateRequest(
                    extra = AliasExtra(
                        creditCardConfig = CreditCardConfig(
                            ccExpiry = creditCardData.expiryMonth.toString() + "/" + creditCardData.expiryYear.toString().takeLast(2),
                            ccMask = creditCardData.number.takeLast(4),
                            ccType = creditCardTypeName,
                            ccHolderName = creditCardData.billingData?.fullName(),
                            encryptedCardNumber = encryptCard.encryptedNumber,
                            encryptedExpiryMonth = encryptCard.encryptedExpiryMonth,
                            encryptedExpiryYear = encryptCard.encryptedExpiryYear,
                            encryptedSecurityCode = encryptCard.encryptedSecurityCode
                        ),
                        paymentMethod = PaymentMethodType.CC.name,
                        personalData = creditCardRegistrationRequest.billingData

                    )
                )
            )
                .subscribeOn(Schedulers.io()).blockingGet()

            when (exchangeAlias.resultCode) {
                "Authorised" -> {
                    it.onSuccess(creditCardRegistrationRequest.aliasId)
                }

                "IdentifyShopper" -> {
                    val action = Threeds2FingerprintAction()
                    action.token = exchangeAlias.token
                    action.paymentData = exchangeAlias.paymentData
                    action.type = exchangeAlias.actionType

                    if (threeDsCompletedSubject.hasComplete()) {
                        threeDsCompletedSubject = PublishSubject.create()
                    }

                    threeDsCompletedSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ _ ->
                            it.onSuccess(creditCardRegistrationRequest.aliasId)
                        }, {
                        })
                    if (threeDsErrorSubject.hasComplete()) {
                        threeDsErrorSubject = PublishSubject.create()
                    }

                    threeDsErrorSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ checkoutError ->
                            it.onError(checkoutError)
                        }, {
                        })
                    activity.startActivity(
                        ThreeDsHandleActivity.createIntent(
                            activity, action,
                            creditCardRegistrationRequest.aliasId
                        )
                    )
                }

                // 4212345678901237
                "RedirectShopper" -> {

                    if (threeDsCompletedSubject.hasComplete()) {
                        threeDsCompletedSubject = PublishSubject.create()
                    }

                    threeDsCompletedSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ _ ->
                            it.onSuccess(creditCardRegistrationRequest.aliasId)
                        }, {
                        })
                    if (threeDsErrorSubject.hasComplete()) {
                        threeDsErrorSubject = PublishSubject.create()
                    }

                    threeDsErrorSubject
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ checkoutError ->
                            it.onError(checkoutError)
                        }, {
                        })

                    // val testUrl = Uri.parse(exchangeAlias.url)
                    //     .buildUpon()
                    //     .appendQueryParameter("MD", exchangeAlias.md)
                    //     .appendQueryParameter("PaReq", exchangeAlias.paReq)
                    //     .appendQueryParameter("TermUrl", exchangeAlias.termsUrl)
                    //     .build().toString()
                    //
                    // val redirectAction = RedirectAction().apply {
                    //     url = testUrl
                    //     paymentData = exchangeAlias.paymentData
                    //     type = exchangeAlias.actionType
                    //     method = "post"
                    // }

                    ThreeDsHandleActivity.createIntent(
                        context = activity,
                        url = exchangeAlias.url,
                        alias = creditCardRegistrationRequest.aliasId,
                        md = exchangeAlias.md,
                        paReq = exchangeAlias.paReq,
                        termsUrl = "https://payment-dev.mblb.net"
                    ).also { intent ->
                        activity.startActivity(intent)
                    }
                }
            }
        }
    }

    fun registerSepa(
        sepaRegistrationRequest: SepaRegistrationRequest
    ): Single<String> {
        val sepaData = sepaRegistrationRequest.sepaData
        val aliasId = sepaRegistrationRequest.aliasId
        val billingData = sepaRegistrationRequest.billingData

        val sepaConfig = SepaConfig(
            iban = sepaData.iban,
            bic = sepaData.bic,
            name = sepaData.billingData?.fullName(),
            lastname = billingData.lastName,
            street = billingData.address1,
            zip = billingData.zip,
            city = billingData.city,
            country = billingData.country
        )
        return mobilabApi.updateAlias(
            aliasId,
            AliasUpdateRequest(
                extra = AliasExtra(sepaConfig = sepaConfig,
                    paymentMethod = PaymentMethodType.SEPA.name,
                    personalData = sepaRegistrationRequest.billingData
                )
            )
        ).andThen(Single.just(aliasId))
    }

    fun getPreparationData(method: PaymentMethodType): Single<Map<String, String>> {
        // Doesn't matter what the method is, token should be returned
        return Single.create {
            try {
                // val parameters = PaymentSetupParametersImpl(application)
                // val result = mapOf(
                //         "token" to parameters.sdkToken,
                //         "channel" to "Android",
                //         "returnUrl" to "app://" // We're not supporting 3ds at the moment, so return URL is never used
                // )
                it.onSuccess(emptyMap())
            } catch (exception: CheckoutException) {
                // This should rarely happen as it is actually just a device fingerprint
                it.onError(OtherException("Generating token failed", originalException = exception))
            }
        }
    }

    @SuppressLint("CheckResult")
    fun handleAdyenThreeDsResult(
        activity: Activity,
        data: ActionComponentData,
        aliasId: String
    ): Single<VerifyThreeDsDto> {
        return Single.create {
            val jsonString = gson.toJson(data.details)
            val result: AdyenThreeDsResult = gson.fromJson(jsonString, AdyenThreeDsResult::class.java)

            val challengeResult = result.nameValuePairs.details.nameValuePairs.challengeResult
            val fingerprint = result.nameValuePairs.details.nameValuePairs.fingerprint
            if (challengeResult != null) {
                mobilabApi.verifyChallenge(
                    aliasId = aliasId,
                    verifyChallengeRequestDto = VerifyChallengeRequestDto(
                        challengeResult = challengeResult
                    )
                ).subscribeOn(Schedulers.io())
                    .subscribe({
                        if (it.resultCode == "Authorised") {
                            threeDsCompletedSubject.onNext(Unit)
                            threeDsCompletedSubject.onComplete()
                            activity.finish()
                        }
                    }, {
                    })
            }
            if (fingerprint != null) {
                it.onSuccess(
                    mobilabApi.verifyThreeDs(
                        aliasId = aliasId,
                        verifyThreeDsRequestDto = VerifyThreeDsRequestDto(
                            fingerprintResult = fingerprint
                        )
                    ).subscribeOn(Schedulers.io())
                        .blockingGet()
                )
            }
        }
    }

    fun onThreeDsError(activity: Activity, checkoutException: CheckoutException) {
        threeDsErrorSubject.onNext(checkoutException)
        threeDsErrorSubject.onComplete()
        activity.finish()
    }

    @SuppressLint("CheckResult")
    fun handleRedirect(activity: Activity, aliasId: String, md: String, paReq: String) {
        mobilabApi.verifyRedirect(aliasId, VerifyRedirectDto(md, paReq))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                if (it.resultCode == "Authorised") {
                    threeDsCompletedSubject.onNext(Unit)
                    threeDsCompletedSubject.onComplete()
                    activity.finish()
                }
            }, {
                Timber.e("redirect error: $it")
            })
    }
}