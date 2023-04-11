package com.mobilabsolutions.stash.sample.data

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 11-09-2019.
 */
interface SamplePreference {

    fun setup()

    var creditCardPreference: Psp

    var sepaPreference: Psp

    enum class Psp {
        ADYEN,
        BRAINTREE,
        BS_PAYONE
    }
}