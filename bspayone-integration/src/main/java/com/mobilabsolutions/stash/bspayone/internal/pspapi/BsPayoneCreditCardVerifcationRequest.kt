/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone.internal.pspapi

import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsPayoneCreditCardVerifcationRequest(
    @Transient
    val baseRequest: BsPayoneBaseRequest,
    @SerializedName("aid")
    val accountId: String,
    @SerializedName("cardpan")
    val cardPan: String,
    @SerializedName("cardtype")
    val cardType: String,
    @SerializedName("cardexpiredate") // TODO add custom expiry date serializer
    val cardExpireDate: LocalDate,
    @SerializedName("cardcvc2")
    val cardCvc: String,
    @SerializedName("storecarddata")
    val storeCardData: String = "yes"

) : BsPayoneBaseRequest(baseRequest) {
    override fun toMap(): MutableMap<String, String> {
        var map = baseRequest.toMap()
        map.putAll(
                mapOf(
                        "aid" to accountId,
                        "cardpan" to cardPan,
                        "cardtype" to cardType,
                        "cardexpiredate" to cardExpireDate.format(DateTimeFormatter.ofPattern("yyMM")),
                        "cardcvc2" to cardCvc,
                        "storecarddata" to storeCardData

                )
        )

        return map
    }
}