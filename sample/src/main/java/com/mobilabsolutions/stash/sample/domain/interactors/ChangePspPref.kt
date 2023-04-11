package com.mobilabsolutions.stash.sample.domain.interactors

import com.mobilabsolutions.stash.sample.data.SamplePreference
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 11-09-2019.
 */
class ChangePspPref @Inject constructor(
    private val samplePreference: SamplePreference
) {

    fun changeCreditCardPref(psp: SamplePreference.Psp) {
        samplePreference.creditCardPreference = psp
    }

    fun changeSepaPref(psp: SamplePreference.Psp) {
        samplePreference.sepaPreference = psp
    }
}