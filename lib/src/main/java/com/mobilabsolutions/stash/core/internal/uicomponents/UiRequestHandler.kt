/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.uicomponents

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mobilabsolutions.stash.core.PaymentMethodAlias
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.R
import com.mobilabsolutions.stash.core.internal.IdempotencyKey
import com.mobilabsolutions.stash.core.internal.PspCoordinator
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.psphandler.Integration
import com.mobilabsolutions.stash.core.model.BillingData
import com.mobilabsolutions.stash.core.model.CreditCardData
import com.mobilabsolutions.stash.core.model.SepaData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * UiRequestHandler is responsible for handling the UI data entry flow. It facilitates this by creating
 * a host activity that can then show either a payment method picker fragment, or request a PSP Integration
 * module to show appropriate payment method data entry fragment.
 *
 * If 3rd party developer provided an activity context, then host activitiy created by this handler
 * will be in the same task as activity which context was provided. Otherwise a new task will be started
 * for the host activity.
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
class UiRequestHandler @Inject constructor() {
    /**
     * Data entry result is used to signal the current state to the data entry fragment
     * launched by the appropriate PSP integration module.
     */
    sealed class DataEntryResult {
        object Success : DataEntryResult()
        object Processing : DataEntryResult()
        data class Failure(val throwable: Throwable) : DataEntryResult()
    }

    /**
     * Exception thrown when user decided to cancel sepcific data entry (i.e. SEPA, Credit Card)
     */
    class EntryCancelled : RuntimeException()

    /**
     * Exception thrown when user decided to cancel the whole flow. Happens in two cases:
     * * A picker was presented, and user backed out of picker screen
     * * A picker was not presented, but a direct data entry screen (i.e. SEPA, Credit Card) and
     * user backed out of it
     */
    class UserCancelled : RuntimeException("User cancelled")

    @Inject
    lateinit var integrations: Map<@JvmSuppressWildcards Integration, @JvmSuppressWildcards Set<@JvmSuppressWildcards PaymentMethodType>>

    @Inject
    lateinit var applicationContext: Context

    internal val processing = AtomicBoolean(false)
    private var currentRequestId = -1

    var hostActivityProvider: ReplaySubject<AppCompatActivity> =
            ReplaySubject.create()

    lateinit var paymentMethodTypeSubject: ReplaySubject<PaymentMethodType>

    var errorSubject: PublishSubject<PaymentMethodAlias> = PublishSubject.create()

    var chooserUsed = false

    lateinit var currentChooserFragment: Fragment

    /**
     * Method that is used by activity to signal that it was created and is then delivered asynchronously through an observable (subject)
     */
    fun provideHostActivity(activity: AppCompatActivity) {
        hostActivityProvider.onNext(activity)
    }

    /**
     * Signalling that the user backed out of the picker and state of the handler should be reset
     */
    fun chooserCancelled() {
        hostActivityProvider = ReplaySubject.create()
        errorSubject.onError(RuntimeException())
        errorSubject = PublishSubject.create()
        processing.set(false)
        chooserUsed = false
    }

    fun closeFlow() {
        hostActivityProvider = ReplaySubject.create()
        errorSubject = PublishSubject.create()
        processing.set(false)
        chooserUsed = false
    }

    /**
     * Signalling that user cancelled data entry and that depending on the state, either
     * a [UserCancelled] or [EntryCancelled] should be thrown
     */
    fun entryCancelled() {
        hostActivityProvider = ReplaySubject.create()
        if (chooserUsed) {
            errorSubject.onError(EntryCancelled())
        } else {
            errorSubject.onError(UserCancelled())
        }
        errorSubject = PublishSubject.create()
        processing.set(false)
    }

    /**
     * Signals that flow is completed and activity should be finished
     */
    private fun flowCompleted(hostActivity: Activity) {
        hostActivity.finish()

        hostActivityProvider = ReplaySubject.create()
        currentRequestId = -1
        processing.set(false)
    }

    internal fun availablePaymentMethods(): List<PaymentMethodType> {
        return integrations.values.flatMap {
            it.toList()
        }
    }

    /**
     * Request ID makes this reentrant from the perspective of PspCoordinators
     */
    fun checkFlow(requestId: Int) {
        // if (processing.compareAndSet(false, true)) {
        //     if (currentRequestId != requestId && currentRequestId != -1) {
        //         throw RuntimeException("Already processing payment method entry")
        //     }
        //     currentRequestId = requestId
        // } else {
        //     if (currentRequestId != requestId) {
        //         throw RuntimeException("Already processing payment method entry")
        //     }
        // }
    }

    /**
     * Launches host activity and returns the activity creation subject as a single
     */
    private fun launchHostActivity(activity: Activity): Single<AppCompatActivity> {
        if (!hostActivityProvider.hasValue()) {
            val launchHostIntent = Intent(activity, RegistrationProcessHostActivity::class.java)
            activity.startActivity(launchHostIntent)
        }
        return hostActivityProvider.firstOrError()
    }

    /**
     * Credit card handling flow. Since the data entry fragments now require that an error is shown
     * based on response from PSP, we need some special handling to be able to process those
     * events and still return a Single<PaymentMethodAlias> as is specified by the API
     */
    private fun handleCreditCardMethodEntryRequest(
        activity: Activity,
        integration: Integration,
        paymentMethodType: PaymentMethodType,
        requestId: Int,
        block: (CreditCardData, AdditionalRegistrationData, IdempotencyKey) -> Single<PaymentMethodAlias>
    ): Single<PaymentMethodAlias> {
        checkFlow(requestId)
        val hostActivitySingle = launchHostActivity(activity)

        val resultSubject = PublishSubject.create<DataEntryResult>()

        return hostActivitySingle.flatMap { hostActivity ->
            (hostActivity as RegistrationProcessHostActivity).setState(
                    RegistrationProcessHostActivity.CurrentState.ENTRY
            )
            integration.handlePaymentMethodEntryRequest(
                    hostActivity,
                    paymentMethodType,
                    AdditionalRegistrationData(),
                    resultSubject
            ).subscribeOn(AndroidSchedulers.mainThread())
                    .flatMap {
                        val idempotencyKey = IdempotencyKey(UUID.randomUUID().toString(), false)
                        val expiryMonth = it.extraData.getValue((CreditCardData.EXPIRY_MONTH)).toInt()
                        val expiryYear = it.extraData.getValue(CreditCardData.EXPIRY_YEAR).toInt()
                        val creditCardData = CreditCardData(
                                it.extraData.getValue(CreditCardData.CREDIT_CARD_NUMBER),
                                expiryMonth,
                                expiryYear,
                                it.extraData.getValue(CreditCardData.CVV),
                                BillingData(
                                        firstName = BillingData.ADDITIONAL_DATA_FIRST_NAME,
                                        lastName = it.extraData.getValue(BillingData.ADDITIONAL_DATA_LAST_NAME))
                        )
                        resultSubject.onNext(DataEntryResult.Processing)
                        block.invoke(creditCardData, it, idempotencyKey)
                                .toObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext {
                                    resultSubject.onNext(DataEntryResult.Success)
                                }
                                .doOnError { throwable ->
                                    resultSubject.onNext(DataEntryResult.Failure(throwable))
                                }
                                .filterNotSuccess()
                    }
                    .firstOrError()
                    .doFinally { flowCompleted(hostActivity) }
                    .ambWith(errorSubject.firstOrError())
        }
    }

    /**
     * Sepa handling flow. Since the data entry fragments now require that an error is shown
     * based on response from PSP, we need some special handling to be able to process those
     * events and still return a Single<PaymentMethodAlias> as is specified by the API
     */
    fun handleSepaMethodEntryRequest(
        activity: Activity,
        integration: Integration,
        paymentMethodType: PaymentMethodType,
        requestId: Int,
        block: (SepaData, AdditionalRegistrationData, IdempotencyKey) -> Single<PaymentMethodAlias>
    ): Single<PaymentMethodAlias> {

        checkFlow(requestId)

        val hostActivitySingle = launchHostActivity(activity)
        val resultSubject = PublishSubject.create<DataEntryResult>()

        return hostActivitySingle.flatMap { hostActivity ->
            (hostActivity as RegistrationProcessHostActivity).setState(
                    RegistrationProcessHostActivity.CurrentState.ENTRY
            )
            integration.handlePaymentMethodEntryRequest(
                    hostActivity,
                    paymentMethodType,
                    AdditionalRegistrationData(),
                    resultSubject
            ).subscribeOn(AndroidSchedulers.mainThread())
                    .flatMap {

                        val idempotencyKey = IdempotencyKey(UUID.randomUUID().toString(), false)

                        val sepaData = SepaData(
                                iban = it.extraData.getValue(SepaData.IBAN),
                                billingData = BillingData(
                                        firstName = it.extraData.getValue(BillingData.ADDITIONAL_DATA_FIRST_NAME),
                                        lastName = it.extraData.getValue(BillingData.ADDITIONAL_DATA_LAST_NAME)
                                )
                        )
                        resultSubject.onNext(DataEntryResult.Processing)
                        block.invoke(sepaData, it, idempotencyKey)
                                .toObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext {
                                    resultSubject.onNext(DataEntryResult.Success)
                                }
                                .doOnError { throwable ->
                                    resultSubject.onNext(DataEntryResult.Failure(throwable))
                                }
                                .filterNotSuccess()
                    }.firstOrError()
                    .doFinally { flowCompleted(hostActivity) }
                    .ambWith(errorSubject.firstOrError())
        }
    }

    /**
     * Credit card handling flow. Since the data entry fragments now require that an error is shown
     * based on response from PSP, we need some special handling to be able to process those
     * events and still return a Single<PaymentMethodAlias> as is specified by the API
     */
    fun handlePaypalMethodEntryRequest(
        activity: Activity,
        integration: Integration,
        additionalRegistrationData: AdditionalRegistrationData,
        requestId: Int
    ): Single<AdditionalRegistrationData> {
        checkFlow(requestId)
        val resultSubject = PublishSubject.create<DataEntryResult>()
        return launchHostActivity(activity).flatMap { hostActivity ->
            (hostActivity as RegistrationProcessHostActivity).setState(
                    RegistrationProcessHostActivity.CurrentState.ENTRY
            )
            integration.handlePaymentMethodEntryRequest(
                    hostActivity,
                    PaymentMethodType.PAYPAL,
                    additionalRegistrationData,
                    resultSubject
            ).firstOrError()
                    .doFinally {
                        flowCompleted(hostActivity)
                    }.ambWith(errorSubject.map {
                        AdditionalRegistrationData()
                    }.firstOrError())
        }
    }

    /**
     * Method picker setup and handling
     */
    fun askUserToChosePaymentMethod(
        activity: Activity,
        requestId: Int
    ): Single<PaymentMethodType> {
        checkFlow(requestId)
        chooserUsed = true
        return launchHostActivity(activity).flatMap { hostActivity ->
            val supportFragmentManager = hostActivity.supportFragmentManager
            (hostActivity as RegistrationProcessHostActivity).setState(
                    RegistrationProcessHostActivity.CurrentState.CHOOSER
            )
            paymentMethodTypeSubject = ReplaySubject.create()
            val paymentMethodChoiceFragment = PaymentMethodChoiceFragment()
            currentChooserFragment = paymentMethodChoiceFragment
            supportFragmentManager.beginTransaction()
                    .add(R.id.host_activity_fragment, paymentMethodChoiceFragment).commitNow()
            paymentMethodTypeSubject
                    .doOnError {
                        flowCompleted(hostActivity)
                    }
                    .doOnNext {
                        supportFragmentManager.beginTransaction().remove(currentChooserFragment)
                                .commitNow()
                    }.firstOrError()
        }
    }

    /**
     * Entry point for requests for CreditCard UI handling from PSP Coordinator
     */
    internal fun registerCreditCardUsingUIComponent(
        activity: Activity,
        pspCoordinator: PspCoordinator,
        requestId: Int
    ): Single<PaymentMethodAlias> {
        val chosenIntegration = integrations.filter {
            it.value.contains(PaymentMethodType.CC)
        }.keys.first()
        return handleCreditCardMethodEntryRequest(
                activity,
                chosenIntegration,
                PaymentMethodType.CC,
                requestId
        ) { creditCardData, additionalUIData, idempotencyKey ->
            pspCoordinator.handleRegisterCreditCard(
                    activity = activity,
                    creditCardData = creditCardData,
                    additionalUIData = additionalUIData,
                    idempotencyKey = idempotencyKey
            )
        }
    }

    /**
     * Entry point for requests for SEPA UI handling from PSP Coordinator
     */
    internal fun registerSepaUsingUIComponent(
        activity: Activity,
        pspCoordinator: PspCoordinator,
        requestId: Int
    ): Single<PaymentMethodAlias> {

        val chosenIntegration = integrations.filter {
            it.value.contains(PaymentMethodType.SEPA)
        }.keys.first()

        return handleSepaMethodEntryRequest(
                activity,
                chosenIntegration,
                PaymentMethodType.SEPA,
                requestId
        ) { sepaData, additionalUIData, idempotencyKey ->
            pspCoordinator.handleRegisterSepa(
                    activity = activity,
                    sepaData = sepaData,
                    additionalUIData = additionalUIData,
                    idempotencyKey = idempotencyKey
            )
        }
    }
}

/**
 * Helper extension to remove all failures from the flow, so we can still deliver Single<PaymentMethodAlias>
 * Achieved by materializeing the event and inspecting if it is error or completion and filtering those out.
 */
private fun <T> Observable<T>.filterNotSuccess(): Observable<T> {
    return materialize()
            .filter {
                !it.isOnError && !it.isOnComplete
            }
            .map { it.value }
}