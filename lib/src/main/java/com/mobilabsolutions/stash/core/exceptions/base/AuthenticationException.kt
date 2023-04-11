/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.exceptions.base

class AuthenticationException(
    override val message: String = "Authentication Failed",
    override val code: Int? = null,
    override val errorTitle: String? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code, originalException)
