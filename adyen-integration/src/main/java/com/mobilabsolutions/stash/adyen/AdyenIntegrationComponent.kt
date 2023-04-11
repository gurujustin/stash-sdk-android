/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.adyen

import com.mobilabsolutions.stash.adyen.internal.uicomponents.AdyenCreditCardDataEntryFragment
import com.mobilabsolutions.stash.adyen.internal.uicomponents.AdyenSepaDataEntryFragment
import com.mobilabsolutions.stash.core.internal.IntegrationScope
import com.mobilabsolutions.stash.core.internal.StashComponent
import dagger.Component

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
@Component(
        dependencies = [StashComponent::class],
        modules = [AdyenModule::class]
)
interface AdyenIntegrationComponent {
    fun inject(integration: AdyenIntegration)

    fun inject(creditCardFragment: AdyenCreditCardDataEntryFragment)

    fun inject(adyenSepaDataEntryFragment: AdyenSepaDataEntryFragment)

    fun inject(threeDsHandleActivity: ThreeDsHandleActivity)
}
