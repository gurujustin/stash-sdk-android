package com.mobilabsolutions.stash.sample.domain.interactors

import android.app.Application
import com.mobilabsolutions.stash.adyen.AdyenIntegration
import com.mobilabsolutions.stash.braintree.BraintreeIntegration
import com.mobilabsolutions.stash.bspayone.BsPayoneIntegration
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.Stash
import com.mobilabsolutions.stash.core.StashConfiguration
import com.mobilabsolutions.stash.core.StashUiConfiguration
import com.mobilabsolutions.stash.core.internal.psphandler.IntegrationCompanion
import com.mobilabsolutions.stash.sample.BuildConfig
import com.mobilabsolutions.stash.sample.R
import com.mobilabsolutions.stash.sample.data.SamplePreference
import com.mobilabsolutions.stash.sample.domain.SuspendingWorkInteractor
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class InitialiseStash @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val application: Application,
    private val samplePreference: SamplePreference
) : SuspendingWorkInteractor<Unit, Boolean>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override suspend fun doWork(params: Unit): Boolean {
        if (Stash.initialised()) {
            return true
        }
        val ccPsp: IntegrationCompanion = when (samplePreference.creditCardPreference) {
            SamplePreference.Psp.ADYEN -> AdyenIntegration
            SamplePreference.Psp.BRAINTREE -> BraintreeIntegration
            SamplePreference.Psp.BS_PAYONE -> BsPayoneIntegration
        }

        val sepaPsp: IntegrationCompanion = when (samplePreference.sepaPreference) {
            SamplePreference.Psp.ADYEN -> AdyenIntegration
            SamplePreference.Psp.BRAINTREE -> BraintreeIntegration
            SamplePreference.Psp.BS_PAYONE -> BsPayoneIntegration
        }
        val stashConfiguration = StashConfiguration(
            publishableKey = BuildConfig.testPublishableKey,
            endpoint = BuildConfig.mobilabBackendUrl,
            integrationList = listOf(
                ccPsp to PaymentMethodType.CC,
                sepaPsp to PaymentMethodType.SEPA,
                BraintreeIntegration to PaymentMethodType.PAYPAL
            ),
            testMode = true,
            stashUiConfiguration = StashUiConfiguration.Builder()
                .setSnackBarBackground(R.color.carnation).build()

        )
        Stash.initialize(
            application,
            stashConfiguration
        )
        return true
    }
}