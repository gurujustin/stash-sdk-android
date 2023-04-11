/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.api.backend

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class ErrorResponse(
    @SerializedName("error") val errorTitle: String,
    @SerializedName("error_code") val code: Int,
    @SerializedName("error_description") val message: String,
    val providerDetails: String?
)