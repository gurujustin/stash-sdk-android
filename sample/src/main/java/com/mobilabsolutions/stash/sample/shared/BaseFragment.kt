/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.shared

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.airbnb.mvrx.MvRxView
import com.airbnb.mvrx.MvRxViewModelStore
import dagger.android.support.DaggerFragment
import java.util.UUID

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
abstract class BaseFragment : DaggerFragment(), MvRxView {

    final override val mvrxViewId: String by lazy { mvrxPersistedViewId }

    private lateinit var mvrxPersistedViewId: String

    override val mvrxViewModelStore by lazy { MvRxViewModelStore(viewModelStore) }

    override fun onCreate(savedInstanceState: Bundle?) {
        mvrxViewModelStore.restoreViewModels(this, savedInstanceState)
        mvrxPersistedViewId = savedInstanceState?.getString(PERSISTED_VIEW_ID_KEY)
                ?: "${this::class.java.simpleName}_${UUID.randomUUID()}"
        super.onCreate(savedInstanceState)
    }

    override val subscriptionLifecycleOwner: LifecycleOwner
        get() = this.viewLifecycleOwnerLiveData.value ?: this

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mvrxViewModelStore.saveViewModels(outState)
        outState.putString(PERSISTED_VIEW_ID_KEY, mvrxViewId)
    }

    override fun onStart() {
        super.onStart()
        postInvalidate()
    }
}

private const val PERSISTED_VIEW_ID_KEY = "mvrx:persisted_view_id"