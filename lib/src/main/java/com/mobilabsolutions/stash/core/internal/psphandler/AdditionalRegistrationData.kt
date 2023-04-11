/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.psphandler

/**
 * First go at modularizing, this is an attempt to future-proof cases where PSPs
 * have uncommon or hard to standardize requirements, or a PSP specific feature is needed
 *
 * The concrete class instances should be created by SDK through integrationInitialization apis
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
data class AdditionalRegistrationData(val extraData: Map<String, String> = emptyMap())