package com.mobilabsolutions.stash.sample.features.home.info

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.core.Stash
import com.mobilabsolutions.stash.sample.data.SamplePreference
import com.mobilabsolutions.stash.sample.domain.interactors.ChangePspPref
import com.mobilabsolutions.stash.sample.domain.interactors.GetPspPreference
import com.mobilabsolutions.stash.sample.shared.BaseViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 11-09-2019.
 */
class InfoViewModel @AssistedInject constructor(
    @Assisted initialState: InfoState,
    private val getPspPreference: GetPspPreference,
    private val changePspPref: ChangePspPref
) : BaseViewModel<InfoState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: InfoState): InfoViewModel
    }

    companion object : MvRxViewModelFactory<InfoViewModel, InfoState> {
        override fun create(viewModelContext: ViewModelContext, state: InfoState): InfoViewModel? {
            val fragment: InfoFragment = (viewModelContext as FragmentViewModelContext).fragment()
            return fragment.infoViewModelFactory.create(state)
        }
    }

    init {
        setState {
            copy(
                creditCardPref = getPspPreference.getCreditCardPref(),
                sepaPref = getPspPreference.getSepaPref(),
                stashInitialized = Stash.initialised()
            )
        }
    }

    fun onCcPspSelected(position: Int) {
        val creditCardPsp = SamplePreference.Psp.values()[position]
        changePspPref.changeCreditCardPref(creditCardPsp)
    }

    fun onSepaPspSelected(position: Int) {
        val sepaPsp = SamplePreference.Psp.values()[position]
        changePspPref.changeSepaPref(sepaPsp)
    }
}