/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.exceptions.registration

import com.mobilabsolutions.stash.core.exceptions.base.BasePaymentException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class RegistrationFailedException(
    override val message: String = "Registration failed",
    override val code: Int? = null,
    override val errorTitle: String? = null,
    override val originalException: Throwable? = null
) : BasePaymentException(message, errorTitle, code)