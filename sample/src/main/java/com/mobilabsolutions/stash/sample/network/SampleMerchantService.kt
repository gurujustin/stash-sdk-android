/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.network

import com.mobilabsolutions.stash.sample.network.request.AuthorizePaymentRequest
import com.mobilabsolutions.stash.sample.network.request.CreatePaymentMethodRequest
import com.mobilabsolutions.stash.sample.network.response.AuthorizePaymentResponse
import com.mobilabsolutions.stash.sample.network.response.CreatePaymentMethodResponse
import com.mobilabsolutions.stash.sample.network.response.CreateUserResponse
import com.mobilabsolutions.stash.sample.network.response.PaymentMethodListResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
interface SampleMerchantService {

    @POST("merchant/v1/user")
    fun createUser(): Call<CreateUserResponse>

    @POST("merchant/v1/payment-method")
    fun createPaymentMethod(
        @Body request: CreatePaymentMethodRequest
    ): Call<CreatePaymentMethodResponse>

    @DELETE("merchant/v1/payment-method/{Payment-Method-Id}")
    fun deletePaymentMethod(
        @Path("Payment-Method-Id") paymentMethodId: String
    ): Call<ResponseBody>

    @GET("merchant/v1/payment-method/{User-Id}")
    fun getPaymentMethods(
        @Path("User-Id") userId: String
    ): Call<PaymentMethodListResponse>

    @PUT("merchant/v1/authorization")
    fun authorizePayment(
        @Header("Idempotent-Key") idempotencyKey: String,
        @Body request: AuthorizePaymentRequest
    ): Call<AuthorizePaymentResponse>
}