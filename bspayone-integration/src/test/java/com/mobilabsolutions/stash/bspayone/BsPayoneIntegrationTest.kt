/*
* Copyright © MobiLab Solutions GmbH
*/

package com.mobilabsolutions.stash.bspayone

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.mobilabsolutions.stash.core.PaymentMethodType
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
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import timber.log.Timber
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
class BsPayoneIntegrationTest {

    @Rule
    @JvmField
    val intentsTestRule = IntentsTestRule(TestActivity::class.java)

    private val testPublishableKey = BuildConfig.testPublishableKey
    private val mobilabBeUrl: String = BuildConfig.mobilabBackendUrl
    private val newBsPayoneUrl = BuildConfig.newBsApiUrl

    private lateinit var activity: Activity

    @Inject
    lateinit var registrationManager: RegistrationManagerImpl

    private val validBillingData: BillingData = BillingData(
            city = "Cologne",
            email = "holder@email.test",
            address1 = "Street 1",
            country = "Germany",
            firstName = "Holder",
            lastName = "Holderman"
    )

    private val validSepaData: SepaData = SepaData(
            bic = "PBNKDEFF",
            iban = "DE63123456791212121212",
            billingData = validBillingData
    )

    private val validCreditCardData = CreditCardData(
            "4111111111111111",
            10,
            2020,
            "123",
            validBillingData
    )

    @Before
    fun setUp() {
        activity = intentsTestRule.activity
        val context = ApplicationProvider.getApplicationContext() as Application
        val methods = setOf(PaymentMethodType.SEPA, PaymentMethodType.CC)
        val integration = BsPayoneIntegration.create(methods)

        val graph = DaggerBsPayoneTestComponent.builder()
                .sslSupportModule(SslSupportModule(null, null))
                .stashModule(StashModule(testPublishableKey, mobilabBeUrl, context, mapOf(integration to methods), true))
                .bsPayoneModule(BsPayoneModule(newBsPayoneUrl))
                .build()

        integration.initialize(graph)

        graph.injectTest(this)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        ShadowLog.stream = System.out
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun testRegisterCreditCard() {
        registrationManager.registerCreditCard(activity, validCreditCardData)
                .subscribeBy(
                        onSuccess = { paymentAlias ->
                            Assert.assertNotNull(paymentAlias)
                            println("Payment aliasId: $paymentAlias")
                        },
                        onError = {
                            Timber.e(it, "Failed")
                            Assert.fail(it.message)
                        }
                )
    }

    @Test
    fun testBSSepaRegistration() {
        registrationManager.registerSepaAccount(
                activity, validSepaData)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { paymentAlias ->
                            Assert.assertNotNull(paymentAlias)
                            println("Payment aliasId: $paymentAlias")
                        }

                ) { error ->

                    Assert.fail(error.message)
                }
    }

    @Test
    fun registerCreditCardFailure() {
        val invalidCreditCardData = CreditCardData(
                "4111111111111112",
                1,
                2021,
                "123",
                validBillingData
        )

        registrationManager.registerCreditCard(activity, invalidCreditCardData).subscribeBy(
                onSuccess = {
                    System.out.print("Test")
                },
                onError = {
                    Assert.fail(it.message)
                }
        )
    }
}