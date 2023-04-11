/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone.internal.pspapi

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsPayoneCardVerificationResponse(
    val status: String,
    @SerializedName("pseudocardpan")
    val cardAlias: String?,
    @SerializedName("truncatedcardpan")
    val truncatedCardPan: String?,
    @SerializedName("cardtype")
    val cardType: String?,
    @SerializedName("cardexpiredate")
    val cardExpiryDate: LocalDate?
)