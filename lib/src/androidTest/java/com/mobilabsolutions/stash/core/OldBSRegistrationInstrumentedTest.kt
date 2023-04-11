package com.mobilabsolutions.stash.core/*
 * Copyright © MobiLab Solutions GmbH
 */

// package com.mobilabsolutions.payment.android.newapi
//
// import android.app.Application
// import android.os.Build
// import androidx.test.InstrumentationRegistry
// import com.jakewharton.threetenabp.AndroidThreeTen
// import com.mobilabsolutions.payment.android.BuildConfig
// import com.mobilabsolutions.payment.android.psdk.internal.*
// import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
// import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone.OldBsPayoneModule
// import com.mobilabsolutions.stash.core.BillingData
// import com.mobilabsolutions.stash.core.CreditCardData
// import com.mobilabsolutions.payment.android.psdk.model.PaymentData
// import com.mobilabsolutions.stash.core.SepaData
// //import com.tspoon.traceur.Traceur
// import dagger.Component
// import io.reactivex.schedulers.Schedulers
// import org.junit.Assert
// import org.junit.Before
// import org.junit.Test
// import org.threeten.bp.LocalDate
// import timber.log.Timber
// import java.util.concurrent.CountDownLatch
// import javax.inject.Inject
// import javax.inject.Singleton
//
//
// /**
// * @author [Ugi](ugi@mobilabsolutions.com)
// */
// class OldBSRegistrationInstrumentedTest {
//
//    val testPublishableKey: String = BuildConfig.oldBsTestKey
//    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
//    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
//    val NEW_BS_PAYONE_URL: String = BuildConfig.newBsApiUrl
//
//    private var validCreditCardData: CreditCardData = CreditCardData(
//            "4111111111111111",
//            LocalDate.of(2021, 1, 1),
//            "123",
//            "Holder Holderman"
//    )
//    private var validSepaData: SepaData = SepaData(
//            bic = "PBNKDEFF",
//            maskedIban = "DE63123456791212121212",
//            holder = "Holder Holderman"
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
//    private var paymentData: PaymentData = PaymentData(
//            amount = 100,
//            currency = "EUR",
//            customerId = "1",
//            reason = "Test payment"
//    )
//
//    @Inject
//    lateinit var registrationManager: RegistrationManagerImpl
//
//    @Inject
//    lateinit var paymentManager: NewPaymentManager
//
//    @Before
//    fun setUp() {
//        val context = InstrumentationRegistry.getContext().applicationContext as Application
//        val graphBuilder = DaggerTestOldBsRegistrationSdkComponent.builder()
//                .stashModule(StashModule(testPublishableKey, MOBILAB_BE_URL, context))
//                .hyperchargeModule(HyperchargeModule())
//                .bsPayoneModule(BsPayoneModule(NEW_BS_PAYONE_URL))
//
//        if (Build.VERSION.SDK_INT < 20) {
//            graphBuilder.sslSupportModule(SslSupportModule(TLSSocketFactoryCompat(), SupportX509TrustManager.getTrustManager()))
//        } else {
//            graphBuilder.sslSupportModule(SslSupportModule())
//        }
//        val graph = graphBuilder.build()
//        graph.injectTest(this)
//        Timber.plant(Timber.DebugTree())
//        AndroidThreeTen.init(InstrumentationRegistry.getContext())
//    }
//
//    @Test
//    fun testBSCardRegistration() {
//
//        Timber.d("Starting test card registration")
//
//        val latch = CountDownLatch(1)
//
//        val registrationDisposable = registrationManager.registerCreditCard(
//                validCreditCardData
//        )
//                .subscribeOn(Schedulers.io())
//                .subscribe { paymentAlias ->
//                    Assert.assertNotNull(paymentAlias)
//                    println("Payment aliasId: $paymentAlias")
//                    latch.countDown()
//
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
//    fun testBSSepaRegistration() {
//        val latch = CountDownLatch(1)
//
//        val registrationDisposable = registrationManager.registerSepaAccount(
//                validSepaData)
//                .subscribeOn(Schedulers.io())
//                .subscribe(
//                        { paymentAlias ->
//                            Assert.assertNotNull(paymentAlias)
//                            println("Payment aliasId: $paymentAlias")
//                            latch.countDown()
//
//                        }
//
//                ) { error ->
//
//                    Assert.fail(error.message)
//
//                    latch.countDown()
//                }
//
//        try {
//            latch.await()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//
//        registrationDisposable.dispose()
//    }
//
// //    @Test
// //    fun testCreditCardRemoval() {
// //        Timber.d("Starting remove card aliasId test")
// //
// //        val latch = CountDownLatch(1)
// //
// //        val registrationDisposable = registrationManager.registerCreditCard(
// //                validCreditCardData
// //        )
// //                .subscribeOn(Schedulers.io())
// //                .flatMap { aliasId ->
// //                    Timber.d("Got aliasId: $aliasId")
// //                    registrationManager.removeCreditCardAlias(aliasId).andThen(Single.just(aliasId))
// //                }.flatMap { aliasId -> paymentManager.executeCreditCardPaymentWithAlias(aliasId, paymentData) }
// //                .subscribeBy(
// //                        onSuccess = { transactionId ->
// //                            Timber.d("Got transaction id after aliasId deletion!")
// //                            latch.countDown()
// //                        },
// //                        onError = { error ->
// //                            Timber.d("Removing aliasId reported an error")
// //
// //                            Assert.assertTrue(error is OtherException)
// //                            Assert.assertEquals(error.message, "Payment method is inactive")
// ////                            if (error is HttpException) {
// ////                                if (error.code() != 400) {
// ////                                    Assert.fail(error.message)
// ////                                    Timber.e(error, "Removing aliasId reported an error")
// ////                                }
// ////                            } else {
// ////                                Assert.fail(error.message)
// ////                                Timber.e(error, "Removing aliasId reported an error")
// ////                            }
// //
// //                            latch.countDown()
// //                        }
// //
// //                )
// //        try {
// //            latch.await()
// //        } catch (e: InterruptedException) {
// //            e.printStackTrace()
// //        }
// //
// //        registrationDisposable.dispose()
// //    }
//
// //    @Test
// //    fun testSepaRemoval() {
// //        Timber.d("Starting remove card aliasId test")
// //
// //        val latch = CountDownLatch(1)
// //
// //        val registrationDisposable = registrationManager.registerSepaAccount(
// //                validSepaData
// //        )
// //                .subscribeOn(Schedulers.io())
// //                .flatMap { aliasId ->
// //                    Timber.d("Got aliasId: $aliasId")
// //                    registrationManager.removeSepaAlias(aliasId).andThen(Single.just(aliasId))
// //                }.flatMap { aliasId -> paymentManager.executeCreditCardPaymentWithAlias(aliasId, paymentData) }
// //                .subscribeBy(
// //                        onSuccess = { transactionId ->
// //                            Timber.d("Got transaction id after aliasId deletion!")
// //                            latch.countDown()
// //                        },
// //                        onError = { error ->
// //                            Timber.d("Removing aliasId reported an error")
// //
// //                            Assert.assertTrue(error is OtherException)
// //                            Assert.assertEquals(error.message, "Payment method is inactive")
// //
// //                            latch.countDown()
// //                        }
// //                )
// //        try {
// //            latch.await()
// //        } catch (e: InterruptedException) {
// //            e.printStackTrace()
// //        }
// //
// //        registrationDisposable.dispose()
// //    }
//
//
// }
//
// @Singleton
// @Component(modules = [SslSupportModule::class, StashModule::class, com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone.OldBsPayoneModule::class, HyperchargeModule::class, BsPayoneModule::class])
// internal interface TestOldBsRegistrationSdkComponent : StashComponent {
//    fun injectTest(test: OldBSRegistrationInstrumentedTest)
// }
//
//
