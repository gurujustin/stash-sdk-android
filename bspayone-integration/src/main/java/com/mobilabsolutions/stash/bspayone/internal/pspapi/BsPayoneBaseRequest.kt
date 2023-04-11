/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone.internal.pspapi

import com.google.gson.annotations.SerializedName

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
abstract class BsPayoneBaseRequest(
    @SerializedName("mid")
    open val merchantId: String,
    @SerializedName("portalid")
    open val portalId: String,
    @SerializedName("api_version")
    open val apiVersion: String,
    open val mode: String,
    open val request: String,
    @SerializedName("responsetype")
    open val responseType: String,
    open val hash: String,
    open val encoding: String

) {
    constructor(baseRequest: BsPayoneBaseRequest) : this(
            baseRequest.merchantId,
            baseRequest.portalId,
            baseRequest.apiVersion,
            baseRequest.mode,
            baseRequest.request,
            baseRequest.responseType,
            baseRequest.hash,
            baseRequest.encoding
    )

    companion object {
        fun instance(
            merchantId: String,
            portalId: String,
            apiVersion: String,
            mode: String,
            request: String,
            responseType: String,
            hash: String,
            encoding: String
        ) = object : BsPayoneBaseRequest(
                merchantId, portalId, apiVersion, mode, request, responseType, hash, encoding
        ) {}
    }

    open fun toMap(): MutableMap<String, String> {
        val map = mutableMapOf(
        "mid" to merchantId,
        "portalid" to portalId,
        "api_version" to apiVersion,
        "mode" to mode,
        "request" to request,
        "responsetype" to responseType,
        "hash" to hash
//        "encoding" to encoding.toUpperCase()
        )
        return map
    }
}