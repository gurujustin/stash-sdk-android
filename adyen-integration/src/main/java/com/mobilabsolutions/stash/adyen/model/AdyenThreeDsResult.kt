package com.mobilabsolutions.stash.adyen.model

import com.google.gson.annotations.SerializedName

data class AdyenThreeDsResult(
    val nameValuePairs: NameValuePairs
)

data class NameValuePairs(
    val paymentData: String,

    @SerializedName("threeds2.fingerprint")
    val fingerprint: String?,

    @SerializedName("threeds2.challengeResult")
    val challengeResult: String?,
    val details: Details
)

data class Details(
    val nameValuePairs: NameValuePairs
)