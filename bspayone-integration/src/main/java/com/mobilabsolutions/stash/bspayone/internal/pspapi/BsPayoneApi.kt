/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone.internal.pspapi

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface BsPayoneApi {
    @POST("/client-api")
    fun executePayoneRequest(@Body registrationRequestCreditCard: BsPayoneCreditCardVerifcationRequest): Single<BsPayoneVerificationBaseResponse>

    @POST("/client-api")
    fun executePayoneRequest(@Body registrationRequestSepa: BsPayoneSepaVerifcationRequest): Single<BsPayoneVerificationBaseResponse>

    @GET("/client-api")
    fun executePayoneRequestGet(@QueryMap queryMap: Map<String, String>): Single<BsPayoneVerificationBaseResponse>
}