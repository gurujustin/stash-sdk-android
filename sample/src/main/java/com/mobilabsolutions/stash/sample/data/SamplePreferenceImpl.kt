package com.mobilabsolutions.stash.sample.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.mobilabsolutions.stash.sample.R
import javax.inject.Inject
import javax.inject.Named

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 11-09-2019.
 */
class SamplePreferenceImpl @Inject constructor(
    private val context: Context,
    @Named("app") private val sharedPreferences: SharedPreferences
) : SamplePreference {
    companion object {
        const val KEY_CC_PSP = "pref_cc_psp"
        const val KEY_SEPA_PSP = "pref_sepa_psp"
    }

    private val defaultPspValue = context.getString(R.string.pref_psp_adyen)

    override fun setup() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override var creditCardPreference: SamplePreference.Psp
        get() = getPspForStorageValue(sharedPreferences.getString(KEY_CC_PSP, defaultPspValue)!!)
        set(value) = sharedPreferences.edit {
            putString(KEY_CC_PSP, getStorageKeyForPsp(value))
        }

    override var sepaPreference: SamplePreference.Psp
        get() = getPspForStorageValue(sharedPreferences.getString(KEY_SEPA_PSP, defaultPspValue)!!)
        set(value) = sharedPreferences.edit {
            putString(KEY_SEPA_PSP, getStorageKeyForPsp(value))
        }

    private fun getStorageKeyForPsp(psp: SamplePreference.Psp) = when (psp) {
        SamplePreference.Psp.ADYEN -> context.getString(R.string.pref_psp_adyen)
        SamplePreference.Psp.BRAINTREE -> context.getString(R.string.pref_psp_braintree)
        SamplePreference.Psp.BS_PAYONE -> context.getString(R.string.pref_psp_bspayone)
    }

    private fun getPspForStorageValue(value: String) = when (value) {
        context.getString(R.string.pref_psp_adyen) -> SamplePreference.Psp.ADYEN
        context.getString(R.string.pref_psp_braintree) -> SamplePreference.Psp.BRAINTREE
        context.getString(R.string.pref_psp_bspayone) -> SamplePreference.Psp.BS_PAYONE
        else -> throw IllegalArgumentException("Invalid creditCardPreference value for PSP")
    }
}