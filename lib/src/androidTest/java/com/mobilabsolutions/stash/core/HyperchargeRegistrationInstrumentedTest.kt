package com.mobilabsolutions.stash.core/*
 * Copyright © MobiLab Solutions GmbH
 */

// package com.mobilabsolutions.payment.android.newapi
//
// import android.app.Application
// import android.os.Build
// import androidx.test.InstrumentationRegistry
// import com.mobilabsolutions.payment.android.BuildConfig
// import com.mobilabsolutions.payment.android.psdk.exceptions.validation.SepaValidationException
// import com.mobilabsolutions.payment.android.psdk.internal.*
// import com.mobilabsolutions.payment.android.psdk.internal.psphandler.bspayone.BsPayoneModule
// import com.mobilabsolutions.payment.android.psdk.internal.psphandler.hypercharge.HyperchargeModule
// import com.mobilabsolutions.payment.android.psdk.integration.bsoldintegration.oldbspayone.OldBsPayoneModule
// import com.mobilabsolutions.stash.core.BillingData
// import com.mobilabsolutions.stash.core.CreditCardData
// import com.mobilabsolutions.payment.android.psdk.model.PaymentData
// import com.mobilabsolutions.stash.core.SepaData
// //import com.tspoon.traceur.Traceur
// import dagger.Component
// import io.reactivex.Single
// import io.reactivex.schedulers.Schedulers
// import org.junit.Assert
// import org.junit.Before
// import org.junit.Ignore
// import org.junit.Rule
// import org.junit.Test
// import org.junit.rules.ExpectedException
// import org.threeten.bp.LocalDate
// import retrofit2.HttpException
// import timber.log.Timber
// import java.util.concurrent.CountDownLatch
// import javax.inject.Inject
// import javax.inject.Singleton
//
//
// /**
// * @author [Ugi](ugi@mobilabsolutions.com)
// */
// @Ignore("Hypercharge tests are failing because backend is not receiving callback on time " +
//        "(instead of inside 60 secconds callbacks are dispatched from Hypercharge ~30 minutes later)")
// class HyperchargeRegistrationInstrumentedTest {
//
//    val testPublishableKey = BuildConfig.newBsTestKey
//    val MOBILAB_BE_URL: String = BuildConfig.mobilabBackendUrl
//    val OLD_BS_PAYONE_URL: String = BuildConfig.oldBsApiUrl
//    val NEW_BS_PAYONE_URL = BuildConfig.newBsApiUrl
//
//    private var validCreditCardData: CreditCardData = CreditCardData(
//            "4200000000000000",
//            LocalDate.of(2021, 1, 1),
//            "123",
//            "Holder Holderman"
//    )
//    private var validSepaData: SepaData = SepaData("PBNKDEFF", "DE42721622981375897982", "Holder Holderman")
//    private var validBillingData: BillingData = BillingData(
//            city = "Cologne",
//            email = "holder@email.test",
//            address1 = "Street 1",
//            country = "Germany",
//            firstName = "Holder",
//            lastName = "Holderman"
//    )
//    private var paymentData: PaymentData = PaymentData(
//            amount = 100,
//            currency = "EUR",
//            customerId = "1",
//            reason = "Test payment - HC 1"
//    )
//
//    @Inject
//    lateinit var paymentManager: NewPaymentManager
//    @Inject
//    lateinit var registrationManager: RegistrationManagerImpl
//
//    @get:Rule
//    var expectedException = ExpectedException.none()
//
//    @Before
//    fun setUp() {
//        val context = InstrumentationRegistry.getContext().applicationContext as Application
//        val graphBuilder = DaggerTestHyperchargeRegistrationSdkComponent.builder()
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
//    }
//
//    @Test
//    fun testHCCardRegistration() {
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
//    fun testHCSepaRegistration() {
//        val latch = CountDownLatch(1)
//
//        val registrationDisposable = registrationManager.registerSepaAccount(
//                validSepaData
//        ).subscribeOn(Schedulers.io())
//                .subscribe { paymentAlias ->
//                    Assert.assertNotNull(paymentAlias)
//                    //                            Assert.assertEquals("1234-SEPA", paymentAlias);
//                    println("Payment aliasId: $paymentAlias")
//                    latch.countDown()
//
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
//    /**
//     * Currently our Hypercharge implementation supports only German SEPA accounts, so we should
//     * fail if we get
//     */
//    @Test
//    fun testRegisterSepaNonGerman() {
//
//        val latch = CountDownLatch(1)
//
//        expectedException.expect(RuntimeException::class.java)
//        expectedException.expectMessage("Only German SEPA accounts are supported with Hypercharge provider")
//
//        val nonGermanSepaData = SepaData("PBNKDEFF", "GR4495706023542089375902674", "Holder Holderman")
//        val registrationDisposable = registrationManager.registerSepaAccount(
//                nonGermanSepaData
//        ).subscribeOn(Schedulers.io())
//                .subscribe(
//                        { paymentAlias ->
//                            Assert.fail("Got success when error was expected")
//                            latch.countDown()
//
//                        }
//                ) { error -> Assert.assertTrue(error is SepaValidationException) }
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
//    @Test
//    fun testCreditCardRemoval() {
//        Timber.d("Starting remove card aliasId test")
//
//        val latch = CountDownLatch(1)
//
//        val registrationDisposable = registrationManager.registerCreditCard(
//                validCreditCardData
//        )
//                .subscribeOn(Schedulers.io())
//                .flatMap { aliasId ->
//                    Timber.d("Got aliasId: $aliasId")
//                    registrationManager.removeCreditCardAlias(aliasId).andThen(Single.just(aliasId))
//                }.flatMap { aliasId -> paymentManager.executeCreditCardPaymentWithAlias(aliasId, paymentData) }
//                .subscribe(
//                        { transactionId ->
//                            Timber.d("Got transaction id after aliasId deletion!")
//                            latch.countDown()
//                        }
//                ) { error ->
//                    Timber.d("Removing aliasId reported an error")
//
//                    if (error is HttpException) {
//                        if (error.code() != 400) {
//                            Assert.fail(error.message)
//                            Timber.e(error, "Removing aliasId reported an error")
//                        }
//                    } else {
//                        Assert.fail(error.message)
//                        Timber.e(error, "Removing aliasId reported an error")
//                    }
//
//
//                    latch.countDown()
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
//    fun testSepaRemoval() {
//        Timber.d("Starting remove card aliasId test")
//
//        val latch = CountDownLatch(1)
//
//
//        val registrationDisposable = registrationManager.registerSepaAccount(
//                validSepaData
//        )
//                .subscribeOn(Schedulers.io())
//                .flatMap { aliasId ->
//                    Timber.d("Got aliasId: $aliasId")
//                    registrationManager.removeSepaAlias(aliasId).andThen(Single.just(aliasId))
//                }.flatMap { aliasId -> paymentManager.executeCreditCardPaymentWithAlias(aliasId, paymentData) }
//                .subscribe(
//                        { transactionId ->
//                            Timber.d("Got transaction id after aliasId deletion!")
//                            latch.countDown()
//                        }
//                ) { error ->
//                    Timber.d("Removing aliasId reported an error")
//
//                    if (error is HttpException) {
//                        if (error.code() != 400) {
//                            Assert.fail(error.message)
//                            Timber.e(error, "Removing aliasId reported an error")
//                        }
//                    } else {
//                        Assert.fail(error.message)
//                        Timber.e(error, "Removing aliasId reported an error")
//                    }
//
//                    latch.countDown()
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
// }
//
// @Singleton
// @Component(modules = [SslSupportModule::class, StashModule::class, OldBsPayoneModule::class, HyperchargeModule::class, BsPayoneModule::class])
// internal interface TestHyperchargeRegistrationSdkComponent : StashComponent {
//    fun injectTest(test: HyperchargeRegistrationInstrumentedTest)
// }
