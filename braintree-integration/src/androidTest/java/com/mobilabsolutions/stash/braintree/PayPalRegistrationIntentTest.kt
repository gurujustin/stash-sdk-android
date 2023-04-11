/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Point
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.mobilabsolutions.stash.braintree.internal.uicomponents.BraintreePayPalActivity
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.internal.SslSupportModule
import com.mobilabsolutions.stash.core.internal.StashComponent
import com.mobilabsolutions.stash.core.internal.StashModule
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import dagger.Component
import io.reactivex.schedulers.Schedulers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
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
class PayPalIntentRegistrationTest {
    val MOBILAB_BACKEND_URL = BuildConfig.mobilabBackendUrl
    val MOBILAB_TEST_PUBLISHABLE_KEY = BuildConfig.testPublishableKey

    @Inject
    lateinit var mobilabApi: MobilabApi

    val context = InstrumentationRegistry.getInstrumentation().context
    val methods = setOf(PaymentMethodType.PAYPAL)
    val initialization = BraintreeIntegration.create(methods)
    internal val component = DaggerTestPayPalIntentRegistrationComponent.builder()
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
    val intentsTestRule = object : IntentsTestRule<BraintreePayPalActivity>(BraintreePayPalActivity::class.java, false, false) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            initialization.initialize(component)
        }
    }

    lateinit var integration: BraintreeIntegration

    @Before
    fun setUp() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val coordinates = Array<Point>(4) { Point(0, 0) }
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
    fun checkBrowserIntent() {
        val intent = Intent()
        val response = mobilabApi.createAlias("BRAINTREE", UUID.randomUUID().toString(), emptyMap())
            .subscribeOn(Schedulers.io())
            .blockingGet()
        intent.putExtra(BraintreeHandler.CLIENT_TOKEN, response.pspExtra[BraintreeHandler.CLIENT_TOKEN])
        intentsTestRule.launchActivity(intent)
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, Intent())
        intending(not(isInternal())).respondWith(result)
        onView(isRoot()).perform(waitFor(5000)) // We need to wait for Braintree SDK to fetch necessary data
        intended(allOf(hasAction(Intent.ACTION_VIEW)))
        Intents.assertNoUnverifiedIntents()
    }
}

@Singleton
@Component(modules = [SslSupportModule::class, StashModule::class])
internal interface TestPayPalIntentRegistrationComponent : StashComponent {
    fun injectTest(test: PayPalIntentRegistrationTest)
}