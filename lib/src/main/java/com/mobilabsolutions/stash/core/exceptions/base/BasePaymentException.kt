/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.exceptions.base

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
abstract class BasePaymentException(
    override val message: String = NO_PROVIDER_MESSAGE,
    open val errorTitle: String?,
    open val code: Int? = null,
    open val originalException: Throwable? = null
) : RuntimeException(message) {
    companion object {
        const val NO_PROVIDER_MESSAGE = "There was no specific message from payment provider supplied"
    }
}