/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal

import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Module
class SslSupportModule(private val sslSocketFactory: SSLSocketFactory?, private val x509TrustManager: X509TrustManager?) {

    constructor() : this(null, null)

    @Provides
    @Singleton
    fun provideSslSupportPackage(): SslSupportPackage {
        return SslSupportPackage(sslSocketFactory != null, sslSocketFactory, x509TrustManager)
    }
}

class SslSupportPackage(
    val useCustomSslSocketFactory: Boolean,
    val sslSocketFactory: SSLSocketFactory?,
    val x509TrustManager: X509TrustManager?
)