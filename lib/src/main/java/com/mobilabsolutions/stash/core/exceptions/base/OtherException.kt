/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.exceptions.base

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
open class OtherException(
    override val message: String = "Other Exception",
    override val code: Int? = null,
    override val errorTitle: String? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code, originalException)