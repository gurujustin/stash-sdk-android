/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.stash.braintree.internal.uicomponents.BraintreeCreditCardActivity
import com.mobilabsolutions.stash.braintree.internal.uicomponents.BraintreePayPalActivity
import com.mobilabsolutions.stash.core.internal.IntegrationScope
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.psphandler.CreditCardRegistrationRequest
import com.mobilabsolutions.stash.core.model.BillingData
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
class BraintreeHandler @Inject constructor() {

    @Inject
    lateinit var applicationContext: Context

    private val processing = AtomicBoolean(false)
    internal var resultSubject = PublishSubject.create<Triple<String, String, String>>()

    companion object {
        const val CARD_DATA = "CARD_DATA"
        const val CLIENT_TOKEN = "clientToken"
        const val CARD_NUMBER = "CARD_NUMBER"
        const val CARD_EXPIRY_MONTH = "CARD_EXPIRY_MONTH"
        const val CARD_EXPIRY_YEAR = "CARD_EXPIRY_YEAR"
        const val CARD_CVV = "CARD_CVV"
        const val CARD_FIRST_NAME = "CARD_FIRST_NAME"
        const val CARD_LAST_NAME = "CARD_LAST_NAME"
    }

    fun tokenizePaymentMethods(
        activity: AppCompatActivity,
        additionalRegistrationData: AdditionalRegistrationData
    ): Single<Triple<String, String, String>> {
        return if (processing.compareAndSet(false, true)) {
            resultSubject = PublishSubject.create()
            val payPalActivityIntent = Intent(activity, BraintreePayPalActivity::class.java)
            payPalActivityIntent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            payPalActivityIntent.putExtra(CLIENT_TOKEN, additionalRegistrationData.extraData[CLIENT_TOKEN])
            activity.startActivity(payPalActivityIntent)
            return resultSubject
                    .doOnEach {
                        Timber.d("Event from PayPal activity $it")
                    }
                    .doFinally {
                        Timber.d("Finalizing")
                        processing.set(false)
                    }
                    .firstOrError()
        } else {
            Single.error(RuntimeException("Braintree PayPal activity already shown!"))
        }
    }

    fun registerCreditCard(
        standardizedData: CreditCardRegistrationRequest,
        additionalData: AdditionalRegistrationData
    ): Single<Triple<String, String, String>> {
        return if (processing.compareAndSet(false, true)) {
            resultSubject = PublishSubject.create()
            val intent = Intent(applicationContext, BraintreeCreditCardActivity::class.java)
            intent.flags += Intent.FLAG_ACTIVITY_NEW_TASK
            // Pass Data
            intent.putExtra(CARD_DATA, hashMapOf(
                    CLIENT_TOKEN to additionalData.extraData[CLIENT_TOKEN],
                    CARD_NUMBER to standardizedData.creditCardData.number,
                    CARD_EXPIRY_MONTH to standardizedData.creditCardData.expiryMonth,
                    CARD_EXPIRY_YEAR to standardizedData.creditCardData.expiryYear,
                    CARD_CVV to standardizedData.creditCardData.cvv,
                    CARD_FIRST_NAME to additionalData.extraData[BillingData.ADDITIONAL_DATA_FIRST_NAME],
                    CARD_LAST_NAME to additionalData.extraData[BillingData.ADDITIONAL_DATA_LAST_NAME]
            ))

            applicationContext.startActivity(intent)
            resultSubject
                    .doOnEach {
                        Timber.d("Event from CC activity $it")
                    }
                    .doFinally {
                        Timber.d("Finalizing")
                        processing.set(false)
                    }
                    .firstOrError()
        } else {
            Single.error(RuntimeException("Braintree CC activity already shown!"))
        }
    }
}