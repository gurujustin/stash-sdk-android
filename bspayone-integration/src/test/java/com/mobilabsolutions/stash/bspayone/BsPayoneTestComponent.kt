/*
* Copyright © MobiLab Solutions GmbH
*/

package com.mobilabsolutions.stash.bspayone

import com.mobilabsolutions.stash.core.internal.SslSupportModule
import com.mobilabsolutions.stash.core.internal.StashComponent
import com.mobilabsolutions.stash.core.internal.StashModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [SslSupportModule::class, StashModule::class, BsPayoneModule::class])
interface BsPayoneTestComponent : StashComponent {
    fun injectTest(test: BsPayoneIntegrationTest)
}