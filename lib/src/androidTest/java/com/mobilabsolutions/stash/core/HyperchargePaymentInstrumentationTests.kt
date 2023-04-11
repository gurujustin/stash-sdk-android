package com.mobilabsolutions.stash.core/*
 * Copyright © MobiLab Solutions GmbH
 */

// package com.mobilabsolutions.payment.android.newapi
//
// import android.app.Application
// import android.os.Build
// import androidx.test.InstrumentationRegistry
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
// import org.junit.Assert
// import org.junit.Before
// import org.junit.Ignore
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
// @Ignore("Hypercharge tests are failing because backend is not receiving callback on time " +
//        "(instead of inside 60 secconds callbacks are dispatched from Hypercharge ~30 minutes later)")
// class HyperchargePaymentInstrumentationTests {
//
//    val testPublishableKey = "PD-HC-nhnEiKIFQiZeVjGCM0HZY3xvaI"
//    private val MOBILAB_BE_URL = "https://pd.mblb.net/api/" //TODO load from configuration (debug, production, etc)
//    private val OLD_BS_PAYONE_URL = "https://test.soap.bs-card-service.com/soap-api/"
//    //    private val NEW_BS_PAYONE_URL = "https://secure.pay1.de/client-api/"
//    private val NEW_BS_PAYONE_URL = "https://webhook.site/a9f7f980-097c-4713-b1c9-3e3fcfbe6b1a/"
//
//    var creditCardAlias = "MKbSUZMYsrakydQszXpcZaZSDozNaFCa"
//    var sepaAlias = "SNhbKApSnqPHxYxUhPpRtEgCBUxqWVLw"
//    //    String creditCardAlias = "oczafuzTpfKDLgMUVPPwsjUoMcGnGott";
//
//    private var paymentData: PaymentData = PaymentData(
//            amount = 100,
//            currency = "EUR",
//            customerId = "1",
//            reason = "Test payment - HC 1"
//    )
//
//    private var validCreditCardData: CreditCardData = CreditCardData(
//            "4200000000000000",
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
//            lastName = "Holderman"
//    )
//    @Inject
//    lateinit var registrationManager: RegistrationManagerImpl
//    @Inject
//    lateinit var paymentManager: NewPaymentManager
//
//
//    @Before
//    fun setUp() {
//        val context = InstrumentationRegistry.getContext().applicationContext as Application
//        val graphBuilder = DaggerTestHyperchargeStashComponent.builder()
//                .stashModule(StashModule(testPublishableKey, MOBILAB_BE_URL, context))
//                .oldBsPayoneModule(OldBsPayoneModule(OLD_BS_PAYONE_URL))
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
//        //Traceur.enableLogging()
//        Timber.plant(Timber.DebugTree())
//
//    }
//
//    @Test
//    fun testHyperchargeCreditCardPayment() {
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
//    fun testHyperchargeSepaPayment() {
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
//    fun testHyperchargeOneTimePayment() {
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
//                    Timber.e(error, "HC credit card payment failed")
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
// }
//
// @Singleton
// @Component(modules = [SslSupportModule::class, StashModule::class, OldBsPayoneModule::class, HyperchargeModule::class, BsPayoneModule::class])
// internal interface TestHyperchargeStashComponent : StashComponent {
//    fun injectTest(test: HyperchargePaymentInstrumentationTests)
// }
