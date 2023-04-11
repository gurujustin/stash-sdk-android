/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data

import com.mobilabsolutions.stash.sample.data.entities.ErrorResult
import com.mobilabsolutions.stash.sample.data.entities.Result
import com.mobilabsolutions.stash.sample.data.entities.Success
import com.mobilabsolutions.stash.sample.data.mappers.Mapper
import com.mobilabsolutions.stash.sample.extensions.bodyOrThrow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Singleton
class RetrofitRunner @Inject constructor() {
    suspend fun <T, E> executeForResponse(mapper: Mapper<T, E>, request: suspend () -> Response<T>): Result<E> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                Success(data = mapper.map(response.bodyOrThrow()))
            } else {
                ErrorResult(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            ErrorResult(e)
        }
    }

    suspend fun <T> executeForServerResponse(request: suspend () -> Response<T>): Result<T> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                Success(data = response.bodyOrThrow())
            } else {
                ErrorResult(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            ErrorResult(e)
        }
    }

    suspend fun <T> executeWithNoResult(request: suspend () -> Response<T>): Result<Unit> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                Success(data = Unit)
            } else {
                ErrorResult(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            ErrorResult(e)
        }
    }
}