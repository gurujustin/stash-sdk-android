/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree.internal.uicomponents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.Card
import com.braintreepayments.api.DataCollector
import com.braintreepayments.api.interfaces.BraintreeCancelListener
import com.braintreepayments.api.interfaces.BraintreeErrorListener
import com.braintreepayments.api.interfaces.ConfigurationListener
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener
import com.braintreepayments.api.models.CardBuilder
import com.braintreepayments.api.models.CardNonce
import com.braintreepayments.api.models.Configuration
import com.braintreepayments.api.models.PaymentMethodNonce
import com.mobilabsolutions.stash.braintree.BraintreeHandler
import com.mobilabsolutions.stash.braintree.BraintreeIntegration
import com.mobilabsolutions.stash.braintree.R
import com.mobilabsolutions.stash.core.exceptions.base.ConfigurationException
import com.mobilabsolutions.stash.core.exceptions.base.OtherException
import com.mobilabsolutions.stash.core.exceptions.base.PspException
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.ReplaySubject
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BraintreeCreditCardActivity : AppCompatActivity(), ConfigurationListener,
    PaymentMethodNonceCreatedListener, BraintreeErrorListener, BraintreeCancelListener {

    @Inject
    lateinit var braintreeHandler: BraintreeHandler

    private val disposables = CompositeDisposable()
    private val deviceFingerprintSubject = ReplaySubject.create<String>()
    private var pointOfNoReturnReached: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BraintreeIntegration.integration?.braintreeIntegrationComponent?.inject(this)
        val cardData: HashMap<String, String> = @Suppress("UNCHECKED_CAST") (intent.getSerializableExtra(BraintreeHandler.CARD_DATA) as HashMap<String, String>)
        val token = cardData[BraintreeHandler.CLIENT_TOKEN]
        setContentView(R.layout.activity_braintree_creedit_card)

        if (token == null) {
            finish()
        }
        val braintreeFragment = BraintreeFragment.newInstance(this, token)

        DataCollector.collectDeviceData(braintreeFragment) {
            if (it != null) {
                deviceFingerprintSubject.onNext(it)
            } else {
                deviceFingerprintSubject.onError(OtherException("Couldn't get Braintree device data"))
            }
        }

        val expirationDate = String.format("%s/%s", cardData[BraintreeHandler.CARD_EXPIRY_MONTH], cardData[BraintreeHandler.CARD_EXPIRY_YEAR])

        val cardBuilder = CardBuilder()
            .cardNumber(cardData[BraintreeHandler.CARD_NUMBER])
            .expirationDate(expirationDate)
            .cvv(cardData[BraintreeHandler.CARD_CVV])
            .cardholderName("${cardData[BraintreeHandler.CARD_FIRST_NAME]} ${cardData[BraintreeHandler.CARD_LAST_NAME]}")
            .firstName(cardData[BraintreeHandler.CARD_FIRST_NAME])
            .lastName(cardData[BraintreeHandler.CARD_FIRST_NAME])

        Card.tokenize(braintreeFragment, cardBuilder)
        pointOfNoReturnReached = true
    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    // We want to disable back press once the request has been sent out, because otherwise it will
    // go back to the main screen and then launch braintree activity
    override fun onBackPressed() {
        if (!pointOfNoReturnReached) {
            super.onBackPressed()
            braintreeHandler.resultSubject.onError(UiRequestHandler.UserCancelled())
        }
    }

    override fun onCancel(requestCode: Int) {
        braintreeHandler.resultSubject.onError(UiRequestHandler.UserCancelled())
        this.finish()
    }

    override fun onConfigurationFetched(configuration: Configuration?) {
        Timber.d("Got configuration")
    }

    override fun onPaymentMethodNonceCreated(paymentMethodNonce: PaymentMethodNonce?) {
        Timber.d("Payment nonce create")
        Timber.d("Type - ${paymentMethodNonce?.javaClass}")
        if (paymentMethodNonce is CardNonce) {
            disposables += deviceFingerprintSubject.firstOrError().subscribeBy(
                onSuccess = {
                    braintreeHandler.resultSubject.onNext(
                        Triple(
                            paymentMethodNonce.lastFour
                                ?: paymentMethodNonce.typeLabel ?: "",
                            paymentMethodNonce.nonce
                                ?: throw RuntimeException("Nonce was null in created method"),
                            it
                        )
                    )
                    braintreeHandler.resultSubject.onComplete()
                },
                onError = {
                    braintreeHandler.resultSubject.onError(it)
                    braintreeHandler.resultSubject.onComplete()
                }
            )
        }

        this.finish()
    }

    override fun onError(error: Exception?) {
        error?.printStackTrace()
        val wrappedException = when (error) {
            is com.braintreepayments.api.exceptions.ConfigurationException -> ConfigurationException(originalException = error)
            is com.braintreepayments.api.exceptions.ErrorWithResponse -> PspException(message = error.localizedMessage, code = error.statusCode, originalException = error)
            else -> OtherException(originalException = error)
        }
        braintreeHandler.resultSubject.onError(wrappedException)
        this.finish()
    }
}