package com.mobilabsolutions.stash.core.internal.api.backend.model

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-08-2019.
 */
data class ExchangeAliasDto(
    val actionType: String,
    val paymentData: String,
    val paymentMethodType: String,
    val resultCode: String,
    val token: String,
    val md: String,
    val paReq: String,
    val url: String,
    val termsUrl: String
)