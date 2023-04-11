/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.psphandler

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
interface DeletionRequest {
    fun getPaymentAlias(): String
}