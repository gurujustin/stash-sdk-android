/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.api.backend.model

import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasExtra

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class ExchangeAliasRequestDto(
    val pspAlias: String? = null,
    val extra: AliasExtra? = null
)
