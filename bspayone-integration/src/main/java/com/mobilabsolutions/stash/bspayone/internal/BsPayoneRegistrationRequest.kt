/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone.internal

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class BsPayoneRegistrationRequest(
    val merchantId: String,
    val hash: String,
    val portalId: String,
    val apiVersion: String,
    val mode: String,
    val request: String,
    val responseType: String,
    val accountId: String
) {
    companion object {
        fun fromMap(map: Map<String, String>): BsPayoneRegistrationRequest {
            return BsPayoneRegistrationRequest(
                merchantId = map["merchantId"] ?: throw RuntimeException("merchantId missing"),
                hash = map["hash"] ?: throw RuntimeException("hash missing"),
                portalId = map["portalId"] ?: throw RuntimeException("portalId missing"),
                apiVersion = map["apiVersion"] ?: throw RuntimeException("apiVersion missing"),
                mode = map["mode"] ?: throw RuntimeException("mode missing"),
                request = map["request"] ?: throw RuntimeException("request missing"),
                responseType = map["responseType"]
                    ?: throw RuntimeException("responseType missing"),
                accountId = map["accountId"] ?: throw RuntimeException("accountId missing")
            )
        }
    }
}