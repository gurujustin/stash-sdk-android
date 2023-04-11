package com.mobilabsolutions.stash.sample.features.home

import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.mobilabsolutions.stash.sample.shared.BaseViewModel
import com.mobilabsolutions.stash.sample.util.AppRxSchedulers
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 16-08-2019.
 */
class HomeActivityViewModel @AssistedInject constructor(
    @Assisted initialState: HomeActivityViewState,
    private val schedulers: AppRxSchedulers
) : BaseViewModel<HomeActivityViewState>(initialState) {
    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: HomeActivityViewState): HomeActivityViewModel
    }

    companion object : MvRxViewModelFactory<HomeActivityViewModel, HomeActivityViewState> {
        override fun create(viewModelContext: ViewModelContext, state: HomeActivityViewState): HomeActivityViewModel? {
            val fragment: HomeActivity = viewModelContext.activity()
            return fragment.homeNavigationViewModelFactory.create(state)
        }
    }
}