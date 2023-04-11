/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone

import com.mobilabsolutions.stash.bspayone.internal.uicomponents.BsPayoneCreditCardDataEntryFragment
import com.mobilabsolutions.stash.bspayone.internal.uicomponents.BsPayoneSepaDataEntryFragment
import com.mobilabsolutions.stash.core.internal.IntegrationScope
import com.mobilabsolutions.stash.core.internal.StashComponent
import dagger.Component

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
@Component(dependencies = arrayOf(StashComponent::class), modules = arrayOf(BsPayoneModule::class))
interface BsPayoneIntegrationComponent {
    fun inject(integration: BsPayoneIntegration)

    fun inject(bsPayoneCreditCardFragment: BsPayoneCreditCardDataEntryFragment)

    fun inject(bsPayoneSepaDataEntryFragment: BsPayoneSepaDataEntryFragment)
}
