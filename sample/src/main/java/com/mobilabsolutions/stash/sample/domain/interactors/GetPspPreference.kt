package com.mobilabsolutions.stash.sample.domain.interactors

import com.mobilabsolutions.stash.sample.data.SamplePreference
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 11-09-2019.
 */
class GetPspPreference @Inject constructor(
    private val samplePreference: SamplePreference
) {
    fun getCreditCardPref() = samplePreference.creditCardPreference
    fun getSepaPref() = samplePreference.sepaPreference
}