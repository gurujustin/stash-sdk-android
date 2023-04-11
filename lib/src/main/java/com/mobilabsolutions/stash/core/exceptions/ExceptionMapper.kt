/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.exceptions

import com.google.gson.Gson
import com.mobilabsolutions.stash.core.exceptions.base.AuthenticationException
import com.mobilabsolutions.stash.core.exceptions.base.BasePaymentException
import com.mobilabsolutions.stash.core.exceptions.base.ConfigurationException
import com.mobilabsolutions.stash.core.exceptions.base.OtherException
import com.mobilabsolutions.stash.core.exceptions.base.PspException
import com.mobilabsolutions.stash.core.exceptions.base.ValidationException
import com.mobilabsolutions.stash.core.exceptions.registration.UnknownException
import com.mobilabsolutions.stash.core.internal.api.backend.ErrorResponse
import retrofit2.HttpException
import timber.log.Timber

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class ExceptionMapper(val gson: Gson) {
    fun mapError(httpException: HttpException): BasePaymentException {
        val errorBody = httpException.response()?.errorBody()?.string()
        Timber.d("Error body $errorBody")
        val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
        return when (httpException.code()) {
            in 1000..1003 -> AuthenticationException(errorResponse.message, errorResponse.code, errorResponse.errorTitle)
            in 2000..2006 -> ValidationException(errorResponse.message, errorResponse.code, errorResponse.errorTitle)
            in 3000..3017 -> ConfigurationException(errorResponse.message, errorResponse.code, errorResponse.errorTitle)
            in 4000..4010 -> PspException(errorResponse.message, errorResponse.code, errorResponse.errorTitle)
            5000 -> OtherException(errorResponse.message, errorResponse.code, errorResponse.errorTitle)
            else -> UnknownException(errorResponse.message, errorResponse.code, errorResponse.errorTitle)
        }
    }
}