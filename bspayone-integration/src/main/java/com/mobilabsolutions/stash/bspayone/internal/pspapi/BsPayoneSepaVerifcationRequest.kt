/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone.internal.pspapi

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsPayoneSepaVerifcationRequest(
    @Transient
    val baseRequest: BsPayoneBaseRequest,
    @SerializedName("aid")
    val accountId: String,
    @SerializedName("maskedIban")
    val iban: String

) : BsPayoneBaseRequest(baseRequest) {
    override fun toMap(): MutableMap<String, String> {
        var map = baseRequest.toMap()
        map.putAll(
                mapOf(
                        "aid" to accountId,
                        "maskedIban" to iban

                )
        )

        return map
    }
}