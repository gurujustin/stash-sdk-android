// /*
// * Copyright © MobiLab Solutions GmbH
// */
//
// package com.mobilabsolutions.stash.adyen
//
// import android.app.Application
// import androidx.test.core.app.ApplicationProvider
// import com.jakewharton.threetenabp.AndroidThreeTen
// import com.mobilabsolutions.stash.core.PaymentMethodType
// import com.mobilabsolutions.stash.core.exceptions.base.ValidationException
// import com.mobilabsolutions.stash.core.internal.RegistrationManagerImpl
// import com.mobilabsolutions.stash.core.internal.SslSupportModule
// import com.mobilabsolutions.stash.core.internal.StashComponent
// import com.mobilabsolutions.stash.core.internal.StashModule
// import com.mobilabsolutions.stash.core.model.BillingData
// import com.mobilabsolutions.stash.core.model.CreditCardData
// import com.mobilabsolutions.stash.core.model.SepaData
// import dagger.Component
// import io.reactivex.rxkotlin.subscribeBy
// import org.junit.Assert
// import org.junit.Assert.assertTrue
// import org.junit.Ignore
// import org.junit.Test
// import timber.log.Timber
// import java.util.concurrent.CountDownLatch
// import javax.inject.Inject
// import javax.inject.Singleton
//
// /**
// * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
// */
// class AdyenTest {
//
//    val testPublishableKey = BuildConfig.testPublishableKey
//    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
//
//    @Inject
//    lateinit var registrationManager: RegistrationManagerImpl
//
//    lateinit var validVisaCreditCardData: CreditCardData
//    lateinit var invalidVisaCreditCardData: CreditCardData
//    lateinit var validMastercardCreditCardData: CreditCardData
//    lateinit var validAmexCreditCardData: CreditCardData
//
//    lateinit var validSepaData: SepaData
//
//    fun setUp() {
//        val context = ApplicationProvider.getApplicationContext() as Application
//        val methods = setOf(PaymentMethodType.SEPA, PaymentMethodType.CC)
//        val integration = AdyenIntegration.create(methods)
//
//        val graph = DaggerAdyenTestStashComponent.builder()
//            .sslSupportModule(SslSupportModule(null, null))
//            .stashModule(StashModule(testPublishableKey, MOBILAB_BE_URL, context, mapOf(integration to methods), true))
//            .adyenModule(AdyenModule())
//            .build()
//
//        integration.initialize(graph)
//
//        AndroidThreeTen.init(context)
//
//        graph.injectTest(this)
//
//        validVisaCreditCardData = CreditCardData(
//            number = "4111111111111111",
//            expiryMonth = 10,
//            expiryYear = 2020,
//            cvv = "737",
//            billingData = BillingData("Holder", "Holdermann")
//        )
//
//        invalidVisaCreditCardData = CreditCardData(
//            number = "4111111111111111",
//            expiryMonth = 10,
//            expiryYear = 2020,
//            cvv = "7372",
//            billingData = BillingData("Holder", "Holdermann")
//        )
//
//        validMastercardCreditCardData = CreditCardData(
//            number = "5555 3412 4444 1115",
//            expiryMonth = 10,
//            expiryYear = 2020,
//            cvv = "737",
//            billingData = BillingData("Holder", "Holdermann")
//        )
//
//        validAmexCreditCardData = CreditCardData(
//            number = "370000000000002",
//            expiryMonth = 10,
//            expiryYear = 2020,
//            cvv = "7373",
//            billingData = BillingData("Holder", "Holdermann")
//        )
//
//        validSepaData = SepaData(
//            billingData = BillingData("Holder", "Holdermann"),
//            iban = "DE63123456791212121212"
//        )
//    }
//
//    @Test
//    fun testVisaRegistration() {
//        setUp()
//        val latch = CountDownLatch(1)
//        registrationManager.registerCreditCard(validVisaCreditCardData).subscribeBy(
//            onSuccess = {
//                assertTrue(it.alias.isNotEmpty())
//                latch.countDown()
//            },
//            onError = {
//                Timber.e(it, "Error")
//                Assert.fail(it.message)
//                latch.countDown()
//            }
//        )
//        latch.await()
//    }
//
//    @Test
//    fun testVisaRegistrationFailure() {
//        setUp()
//        val latch = CountDownLatch(1)
//        registrationManager.registerCreditCard(invalidVisaCreditCardData).subscribeBy(
//            onSuccess = {
//                Assert.fail("Expected validation throwable")
//                latch.countDown()
//            },
//            onError = {
//                Timber.e(it, "Error")
//                Assert.assertTrue(it is ValidationException)
//                latch.countDown()
//            }
//        )
//        latch.await()
//    }
//
//    @Test
//    fun testMastercardRegistration() {
//        setUp()
//        val latch = CountDownLatch(1)
//        registrationManager.registerCreditCard(validMastercardCreditCardData).subscribeBy(
//            onSuccess = {
//                assertTrue(it.alias.isNotEmpty())
//                latch.countDown()
//            },
//            onError = {
//                Timber.e(it, "Error")
//                Assert.fail(it.message)
//                latch.countDown()
//            }
//        )
//        latch.await()
//    }
//
//    @Test
//    fun testAmexRegistration() {
//        setUp()
//        val latch = CountDownLatch(1)
//        registrationManager.registerCreditCard(validAmexCreditCardData).subscribeBy(
//            onSuccess = {
//                assertTrue(it.alias.isNotEmpty())
//                latch.countDown()
//            },
//            onError = {
//                Timber.e(it, "Error")
//                Assert.fail(it.message)
//                latch.countDown()
//            }
//        )
//        latch.await()
//    }
//
//    @Ignore("While backend catches up")
//    @Test
//    fun testSepaRegistration() {
//        setUp()
//        val latch = CountDownLatch(1)
//        registrationManager.registerSepaAccount(validSepaData)
//            .subscribeBy(
//                onSuccess = {
//                    assertTrue(it.alias.isNotEmpty())
//                    latch.countDown()
//                },
//                onError = {
//                    Timber.e(it, "Error")
//                    Assert.fail(it.message)
//                    latch.countDown()
//                }
//            )
//        latch.await()
//    }
// }
//
// @Singleton
// @Component(modules = [SslSupportModule::class, StashModule::class, AdyenModule::class])
// internal interface AdyenTestStashComponent : StashComponent {
//    fun injectTest(test: AdyenTest)
// }
