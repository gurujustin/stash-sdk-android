package com.mobilabsolutions.stash.core/*
 * Copyright © MobiLab Solutions GmbH
 */

// package com.mobilabsolutions.payment.android.newapi
//
// import android.app.Application
// import android.os.Build
// import androidx.test.InstrumentationRegistry
// import com.mobilabsolutions.payment.android.BuildConfig
// import com.mobilabsolutions.stash.core.OtherException
// import com.mobilabsolutions.payment.android.psdk.internal.*
// import com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone.BsPayoneModule
// import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
// import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone.OldBsPayoneModule
// import com.mobilabsolutions.stash.core.BillingData
// import com.mobilabsolutions.stash.core.CreditCardData
// import com.mobilabsolutions.payment.android.psdk.model.PaymentData
// //import com.tspoon.traceur.Traceur
// import dagger.Component
// import io.reactivex.schedulers.Schedulers
// import org.junit.Assert;
// import org.junit.Before
// import org.junit.Test
// import org.threeten.bp.LocalDate
// import timber.log.Timber
// import java.util.concurrent.CountDownLatch
// import javax.inject.Inject
// import javax.inject.Singleton
//
// /**
// * @author [Ugi](ugi@mobilabsolutions.com)
// */
// class OldBSPaymentInstrumentationTests {
//    //QEvuprLEfAesqaVxfHOmNxRtKFNAaVyX
//    //mOQIxngxMduuvqgTlBycdKHfCpMqwTCy
//
//    val testPublishableKey: String = BuildConfig.oldBsTestKey
//    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
//    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
//    val NEW_BS_PAYONE_URL: String = BuildConfig.newBsApiUrl
//
//    //    var creditCardAlias = BuildConfig.oldBsExistingCcAlias
//    var creditCardAlias = "QEvuprLEfAesqaVxfHOmNxRtKFNAaVyX"
// //    var sepaAlias = BuildConfig.oldBsExistingSepaAlias
//    var sepaAlias = "YUJokVWXCbvGTLVNxhBtoYdpcoNzCGMr"
// //    var sepaAlias = "wIChkvSAAPLLhLrAHNdvTjeDjibfhoDc"
//    var paymentData: PaymentData = PaymentData(
//            amount = 100,
//            currency = "EUR",
//            customerId = "1",
//            reason = "Test payment"
//    )
//
//    private var validCreditCardData: CreditCardData = CreditCardData(
//            "4111111111111111",
//            LocalDate.of(2021, 1, 1),
//            "123",
//            "Holder Holderman"
//    )
//    private var validBillingData: BillingData = BillingData(
//            city = "Cologne",
//            email = "holder@email.test",
//            address1 = "Street 1",
//            country = "Germany",
//            firstName = "Holder",
//            lastName = "Holderman",
//            zip = "12345"
//    )
//
//    @Inject
//    lateinit var paymentManager: NewPaymentManager
//
//    @Inject
//    lateinit var registrationManager: RegistrationManagerImpl
//
//    @Before
//    fun setUp() {
//        val context = InstrumentationRegistry.getContext().applicationContext as Application
//        val graphBuilder = DaggerTestOldBsStashComponent.builder()
//                .stashModule(StashModule(testPublishableKey, MOBILAB_BE_URL, context))
//                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
//                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))
//                .hyperchargeModule(HyperchargeModule())
//
//        if (Build.VERSION.SDK_INT < 20) {
//            graphBuilder.sslSupportModule(SslSupportModule(TLSSocketFactoryCompat(), SupportX509TrustManager.getTrustManager()))
//        } else {
//            graphBuilder.sslSupportModule(SslSupportModule())
//        }
//        val graph = graphBuilder.build()
//        graph.injectTest(this)
//        //Traceur.enableLogging()
//        Timber.plant(Timber.DebugTree())
//
//    }
//
//    @Test
//    fun testBSCreditCardPayment() {
//        val latch = CountDownLatch(1)
//
//        val paymentDisposable = paymentManager.executeCreditCardPaymentWithAlias(
//                creditCardAlias,
//                paymentData
//        )
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                        { transactionId ->
//                            Assert.assertNotNull(transactionId)
//                            println("Transaction: $transactionId")
//                            latch.countDown()
//
//                        }
//                ) { error ->
//                    Timber.e(error, "BS credit card payment failed")
//                    Assert.fail(error.message)
//                }
//        try {
//            latch.await()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//
//        paymentDisposable.dispose()
//    }
//
//    @Test
//    fun testBSSepaPayment() {
//        val latch = CountDownLatch(1)
//
//        val paymentDisposable = paymentManager.executeSepaPaymentWithAlias(
//                sepaAlias,
//                paymentData
//        )
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                        { transactionId ->
//                            Assert.assertNotNull(transactionId)
//                            println("Transaction: $transactionId")
//                            latch.countDown()
//
//                        }
//                ) { error ->
//                    Timber.e(error, "BS sepa payment failed")
//                    Assert.fail(error.message)
//                }
//        try {
//            latch.await()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//
//        paymentDisposable.dispose()
//    }
//
//    @Test
//    fun testBSOneTimePayment() {
//        val latch = CountDownLatch(1)
//
//        val registrationDisposable = paymentManager.executeCreditCardPayment(
//                validCreditCardData,
//                paymentData
//        )
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                        { transactionId ->
//                            Assert.assertNotNull(transactionId)
//                            println("Transaction: $transactionId")
//                            latch.countDown()
//
//                        }
//                ) { error ->
//                    Timber.e(error, "BS credit card payment failed")
//                    Assert.fail(error.message)
//                }
//        try {
//            latch.await()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//
//        registrationDisposable.dispose()
//    }
//
//    @Test
//    fun testBSCreditCardPaymentFailure() {
//        val latch = CountDownLatch(1)
//
//        val paymentDisposable = paymentManager.executeCreditCardPaymentWithAlias(
//                "1",
//                paymentData
//        )
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                        { transactionId ->
//                            Assert.assertNotNull(transactionId)
//                            println("Transaction: $transactionId")
//                            latch.countDown()
//                            Assert.fail("Expected failure")
//
//                        }
//                ) { error ->
//                    when (error) {
//                        is OtherException ->
//                            Timber.e(error, "BS credit card payment failed " +
//                                    "${error.message} ${error.providerMessage}")
//                        else -> {
//                            Timber.e("Unexpected error!")
//                            Assert.fail("Unexpected error")
//                        }
//
//                    }
//                    latch.countDown()
//
//
//                }
//        try {
//            latch.await()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//
//        paymentDisposable.dispose()
//    }
//
// }
//
// @Singleton
// @Component(modules = [SslSupportModule::class, StashModule::class, OldBsPayoneModule::class, HyperchargeModule::class, BsPayoneModule::class])
// internal interface TestOldBsStashComponent : StashComponent {
//    fun injectTest(test: OldBSPaymentInstrumentationTests)
// }