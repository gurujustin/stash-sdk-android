/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Point
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.mobilabsolutions.stash.braintree.internal.uicomponents.BraintreeCreditCardActivity
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.internal.SslSupportModule
import com.mobilabsolutions.stash.core.internal.StashComponent
import com.mobilabsolutions.stash.core.internal.StashModule
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import dagger.Component
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class CreditCardRegistrationTest {
    private val MOBILAB_BACKEND_URL = BuildConfig.mobilabBackendUrl
    private val MOBILAB_TEST_PUBLISHABLE_KEY = BuildConfig.testPublishableKey

    @Inject
    lateinit var api: MobilabApi

    val context: Context = InstrumentationRegistry.getInstrumentation().context
    private val methods = setOf(PaymentMethodType.CC)
    val initialization = BraintreeIntegration.create(methods)
    internal val component = DaggerTestCreditCardRegistrationComponent.builder()
        .stashModule(StashModule(
            MOBILAB_TEST_PUBLISHABLE_KEY,
            MOBILAB_BACKEND_URL,
            context.applicationContext as Application,
            mapOf(initialization to methods), true))
        .build()

    init {
        component.injectTest(this)
    }

    @get:Rule
    val activityRule = object : ActivityTestRule<BraintreeCreditCardActivity>(BraintreeCreditCardActivity::class.java, true, false) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            initialization.initialize(component)
        }
    }

    @Before
    fun setUp() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val coordinates = Array(4) { Point(0, 0) }
        coordinates[0] = Point(248, 1520)
        coordinates[1] = Point(248, 929)
        coordinates[2] = Point(796, 1520)
        coordinates[3] = Point(796, 929)

        if (!uiDevice.isScreenOn) {
            uiDevice.wakeUp()
            uiDevice.swipe(coordinates, 10)
        }
    }

    @Ignore("Failing on travis, seems that emulator screen goes to sleep")
    @Test
    fun checkLoading() {
        val intent = Intent()
        val response = api.createAlias("BRAINTREE", UUID.randomUUID().toString(), emptyMap())
            .subscribeOn(Schedulers.io())
            .blockingGet()
        intent.putExtra(BraintreeHandler.CARD_DATA, hashMapOf(
            BraintreeHandler.CLIENT_TOKEN to response.pspExtra[BraintreeHandler.CLIENT_TOKEN],
            BraintreeHandler.CARD_NUMBER to "4111111111111111",
            BraintreeHandler.CARD_EXPIRY_MONTH to "07",
            BraintreeHandler.CARD_EXPIRY_YEAR to "2019",
            BraintreeHandler.CARD_CVV to "123",
            BraintreeHandler.CARD_FIRST_NAME to "First",
            BraintreeHandler.CARD_LAST_NAME to "Last"
        ))
        activityRule.launchActivity(intent)
        onView(withId(R.id.credit_card_progress)).check(matches(isDisplayed()))
    }
}

@Singleton
@Component(modules = [SslSupportModule::class, StashModule::class])
internal interface TestCreditCardRegistrationComponent : StashComponent {
    fun injectTest(test: CreditCardRegistrationTest)
}