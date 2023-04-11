/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.exceptions.registration

import com.mobilabsolutions.stash.core.exceptions.base.OtherException

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class UnknownException(
    override val message: String = "Unknown Exception",
    override val code: Int? = null,
    override val errorTitle: String? = null,
    override val originalException: Throwable? = null
) : OtherException(message, code, errorTitle, originalException)