/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.api.backend.v1

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class AliasResponse(
    val aliasId: String,
    val extra: AliasExtra,
    @SerializedName("psp")
    val pspExtra: Map<String, String>
)
