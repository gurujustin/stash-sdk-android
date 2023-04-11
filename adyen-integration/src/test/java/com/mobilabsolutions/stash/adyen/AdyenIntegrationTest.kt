/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.adyen

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.exceptions.base.ValidationException
import com.mobilabsolutions.stash.core.internal.RegistrationManagerImpl
import com.mobilabsolutions.stash.core.internal.SslSupportModule
import com.mobilabsolutions.stash.core.internal.StashModule
import com.mobilabsolutions.stash.core.model.BillingData
import com.mobilabsolutions.stash.core.model.CreditCardData
import com.mobilabsolutions.stash.core.model.SepaData
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import timber.log.Timber
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 30-05-2019.
 */
@RunWith(RobolectricTestRunner::class)
class AdyenIntegrationTest {

    @Rule
    @JvmField
    val intentsTestRule = IntentsTestRule(AdyenTestActivity::class.java)

    val testPublishableKey = BuildConfig.testPublishableKey
    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl

    @Inject
    lateinit var registrationManager: RegistrationManagerImpl

    private val validVisaCreditCardData =
            CreditCardData(
                    number = "4917610000000000",
                    expiryMonth = 10,
                    expiryYear = 2020,
                    cvv = "737",
                    billingData = BillingData("Holder", "Holdermann")
            )
    lateinit var invalidVisaCreditCardData: CreditCardData
    lateinit var validMastercardCreditCardData: CreditCardData
    lateinit var validAmexCreditCardData: CreditCardData

    lateinit var validSepaData: SepaData

    private lateinit var adyenTestActivity: AdyenTestActivity

    @Before
    fun setUp() {
        adyenTestActivity = intentsTestRule.activity
        val context = ApplicationProvider.getApplicationContext() as Application
        val methods = setOf(PaymentMethodType.SEPA, PaymentMethodType.CC)
        val integration = AdyenIntegration.create(methods)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        val graph = DaggerAdyenIntegrationTestComponent.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .stashModule(StashModule(testPublishableKey, MOBILAB_BE_URL, context, mapOf(integration to methods), true))
                .build()

        integration.initialize(graph)

        AndroidThreeTen.init(context)

        graph.injectTest(this)

        invalidVisaCreditCardData = CreditCardData(
                number = "4111111111111111",
                expiryMonth = 10,
                expiryYear = 2020,
                cvv = "7372",
                billingData = BillingData("Holder", "Holdermann")
        )

        validMastercardCreditCardData = CreditCardData(
                number = "5555 3412 4444 1115",
                expiryMonth = 10,
                expiryYear = 2020,
                cvv = "737",
                billingData = BillingData("Holder", "Holdermann")
        )

        validAmexCreditCardData = CreditCardData(
                number = "370000000000002",
                expiryMonth = 10,
                expiryYear = 2020,
                cvv = "7373",
                billingData = BillingData("Holder", "Holdermann")
        )

        validSepaData = SepaData(
                billingData = BillingData("Holder", "Holdermann"),
                iban = "DE63123456791212121212"
        )

        ShadowLog.stream = System.out
    }

    @Test
    fun testVisaRegistration() {
        registrationManager.registerCreditCard(
                activity = adyenTestActivity,
                creditCardData = validVisaCreditCardData
        )
                .subscribeBy(
                        onSuccess = {
                            println("Got result ${it.alias}")
                            assertTrue(it.alias.isNotEmpty())
                        },
                        onError = {
                            Timber.e(it, "Error")
                            fail(it.message)
                        }
                )
    }

    @Test
    fun testVisaRegistrationFailure() {
        registrationManager.registerCreditCard(
                adyenTestActivity,
                invalidVisaCreditCardData
        ).subscribeBy(
                onSuccess = {
                    fail("Expected validation throwable")
                },
                onError = {
                    Timber.e(it, "Error")
                    assertTrue(it is ValidationException)
                }
        )
    }

    @Test
    fun testMastercardRegistration() {
        registrationManager.registerCreditCard(
                adyenTestActivity,
                validMastercardCreditCardData
        ).subscribeBy(
                onSuccess = {
                    assertTrue(it.alias.isNotEmpty())
                },
                onError = {
                    Timber.e(it, "Error")
                    fail(it.message)
                }
        )
    }

    @Test
    fun testAmexRegistration() {
        registrationManager.registerCreditCard(
                adyenTestActivity,
                validAmexCreditCardData).subscribeBy(
                onSuccess = {
                    assertTrue(it.alias.isNotEmpty())
                },
                onError = {
                    Timber.e(it, "Error")
                    fail(it.message)
                }
        )
    }

    @Test
    fun testSepaRegistration() {
        registrationManager.registerSepaAccount(
                adyenTestActivity,
                validSepaData
        ).subscribeBy(
                onSuccess = {
                    assertTrue(it.alias.isNotEmpty())
                },
                onError = {
                    Timber.e(it, "Error")
                    fail(it.message)
                }
        )
    }
}