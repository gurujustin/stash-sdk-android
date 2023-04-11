/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone.internal

import com.mobilabsolutions.stash.core.exceptions.registration.RegistrationFailedException
import com.mobilabsolutions.stash.core.exceptions.registration.UnknownException
import com.mobilabsolutions.stash.bspayone.internal.pspapi.BsPayoneVerificationErrorResponse
import com.mobilabsolutions.stash.bspayone.internal.pspapi.BsPayoneVerificationInvalidResponse

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
object BsPayoneErrorHandler {

    fun handleError(error: BsPayoneVerificationErrorResponse): Throwable {
        return when (error.errorCode) {
            7, 43, 56, 62, 63, 880 -> RegistrationFailedException(error.customerMessage
                ?: "No custom message provided", error.errorCode)
            else -> UnknownException(error.customerMessage
                ?: "No custom message provided", error.errorCode)
        }
    }

    fun handleError(error: BsPayoneVerificationInvalidResponse): Throwable {
        return when (error.errorCode) {
            14 -> RegistrationFailedException(error.customerMessage
                ?: "No custom message provided", error.errorCode)
            else -> UnknownException(error.customerMessage
                ?: "No custom message provided", error.errorCode)
        }
    }
}