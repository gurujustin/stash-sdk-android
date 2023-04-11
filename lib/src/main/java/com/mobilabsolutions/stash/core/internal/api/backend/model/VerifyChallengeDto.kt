package com.mobilabsolutions.stash.core.internal.api.backend.model

data class VerifyChallengeDto(
    val actionType: String,
    val paymentData: String,
    val paymentMethodType: String,
    val resultCode: String,
    val token: String
)