/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.api.backend

import com.mobilabsolutions.stash.core.internal.api.backend.model.CreateAliasResponseDto
import com.mobilabsolutions.stash.core.internal.api.backend.model.ExchangeAliasDto
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyChallengeRequestDto
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyRedirectDto
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyThreeDsDto
import com.mobilabsolutions.stash.core.internal.api.backend.model.VerifyThreeDsRequestDto
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasResponse
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasUpdateRequest
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

interface MobilabApi {
    @POST("alias")
    fun createAlias(
        @Header("PSP-Type") psp: String,
        @Header("Idempotent-Key") idempotencyKey: String,
        @Body dynamicPspConfig: Map<String, String>
    ): Single<AliasResponse>

    @PUT("alias/{aliasId}")
    fun updateAlias(
        @Path("aliasId") aliasId: String,
        @Body aliasUpdateRequest: AliasUpdateRequest
    ): Completable

    @POST("alias")
    fun createAlias(
        @Header("PSP-Type") psp: String,
        @Header("Idempotent-Key") idempotencyKey: String
    ): Single<CreateAliasResponseDto>

    @PUT("alias/{aliasId}")
    fun exchangeAlias(
        @Path("aliasId") aliasId: String,
        @Body aliasUpdateRequest: AliasUpdateRequest
    ): Single<ExchangeAliasDto>

    @POST("alias/{aliasId}/verify")
    fun verifyThreeDs(
        @Path("aliasId") aliasId: String,
        @Body verifyThreeDsRequestDto: VerifyThreeDsRequestDto
    ): Single<VerifyThreeDsDto>

    @POST("alias/{aliasId}/verify")
    fun verifyChallenge(
        @Path("aliasId") aliasId: String,
        @Body verifyChallengeRequestDto: VerifyChallengeRequestDto
    ): Single<VerifyThreeDsDto>

    @POST("alias/{aliasId}/verify")
    fun verifyRedirect(
        @Path("aliasId") aliasId: String,
        @Body verifyRedirectDto: VerifyRedirectDto
    ): Single<VerifyThreeDsDto>
}