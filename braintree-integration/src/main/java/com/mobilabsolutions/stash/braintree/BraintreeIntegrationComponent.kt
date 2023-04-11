/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree

import com.mobilabsolutions.stash.braintree.internal.uicomponents.BraintreeCreditCardActivity
import com.mobilabsolutions.stash.braintree.internal.uicomponents.BraintreeCreditCardDataEntryFragment
import com.mobilabsolutions.stash.braintree.internal.uicomponents.BraintreePayPalActivity
import com.mobilabsolutions.stash.core.internal.IntegrationScope
import com.mobilabsolutions.stash.core.internal.StashComponent
import dagger.Component

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
@Component(dependencies = [StashComponent::class], modules = [BraintreeModule::class])
interface BraintreeIntegrationComponent {
    fun inject(integration: BraintreeIntegration)

    fun inject(payPalActivity: BraintreePayPalActivity)

    fun inject(creditCardActivity: BraintreeCreditCardActivity)

    fun inject(braintreeCreditCardDataEntryFragment: BraintreeCreditCardDataEntryFragment)
}
