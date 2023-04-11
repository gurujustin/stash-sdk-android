/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.api.backend

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class PayoneSpecificData(
    override val psp: String,
    val merchantId: String,
    val hash: String,
    val portalId: String,
    val apiVersion: String,
    val mode: String,
    val request: String,
    val responseType: String,
    val accountId: String

) : ProviderSpecificData()