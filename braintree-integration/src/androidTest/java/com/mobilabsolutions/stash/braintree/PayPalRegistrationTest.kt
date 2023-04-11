/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree

import android.app.Application
import android.content.Intent
import android.graphics.Point
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import com.mobilabsolutions.stash.braintree.internal.uicomponents.BraintreePayPalActivity
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
class PayPalRegistrationTest {
    val MOBILAB_BACKEND_URL = BuildConfig.mobilabBackendUrl
    val MOBILAB_TEST_PUBLISHABLE_KEY = BuildConfig.testPublishableKey

    @Inject
    lateinit var mobilabApi: MobilabApi

    val context = InstrumentationRegistry.getInstrumentation().context
    val methods = setOf(PaymentMethodType.PAYPAL)
    val initialization = BraintreeIntegration.create(methods)
    internal val component = DaggerTestPayPalRegistrationComponent.builder()
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
    val activityRule = object : ActivityTestRule<BraintreePayPalActivity>(BraintreePayPalActivity::class.java, true, false) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            initialization.initialize(component)
        }
    }

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
    fun checkLoading() {
        val intent = Intent()
        val response = mobilabApi.createAlias("BRAINTREE", UUID.randomUUID().toString(), emptyMap())
            .subscribeOn(Schedulers.io())
            .blockingGet()
        intent.putExtra(BraintreeHandler.CLIENT_TOKEN, response.pspExtra[BraintreeHandler.CLIENT_TOKEN])
        activityRule.launchActivity(intent)
        onView(withId(R.id.paypal_progress)).check(matches(isDisplayed()))
    }
}

@Singleton
@Component(modules = [SslSupportModule::class, StashModule::class])
internal interface TestPayPalRegistrationComponent : StashComponent {
    fun injectTest(test: PayPalRegistrationTest)
}