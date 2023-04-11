package com.mobilabsolutions.stash.core.internal.api.backend.model

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 13-08-2019.
 */
data class ChallengeShopperResponseDto(
    val actionType: String,
    val challengeToken: String,
    val paymentData: String,
    val paymentMethodType: String,
    val resultCode: String
)