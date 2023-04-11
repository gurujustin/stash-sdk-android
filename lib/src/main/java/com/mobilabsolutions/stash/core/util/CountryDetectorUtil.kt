/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.util

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
object CountryDetectorUtil {
    fun getBestGuessAtCurrentCountry(context: Context): Locale {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = telephonyManager.simCountryIso
        if (simCountry == "") return Locale.getDefault()
        return Locale("", simCountry)
    }
}