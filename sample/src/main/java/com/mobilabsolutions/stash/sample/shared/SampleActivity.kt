package com.mobilabsolutions.stash.sample.shared

import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.airbnb.mvrx.BaseMvRxActivity
import com.airbnb.mvrx.MvRxView
import com.airbnb.mvrx.MvRxViewModelStore
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.UUID
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 16-08-2019.
 */
abstract class SampleActivity : BaseMvRxActivity(), HasAndroidInjector, MvRxView {

    override val mvrxViewModelStore by lazy { MvRxViewModelStore(viewModelStore) }

    override fun androidInjector(): AndroidInjector<Any> = fragmentInjector

    final override val mvrxViewId
        get() = mvrxPersistedViewId

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Any>

    private lateinit var mvrxPersistedViewId: String

    override val subscriptionLifecycleOwner: LifecycleOwner
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        mvrxViewModelStore.restoreViewModels(this, savedInstanceState)
        super.onCreate(savedInstanceState)

        mvrxPersistedViewId = savedInstanceState?.getString(PERSISTED_VIEW_ID_KEY)
            ?: "${this::class.java.simpleName}_${UUID.randomUUID()}"
    }

    override fun onStart() {
        super.onStart()
        // This ensures that invalidate() is called for static screens that don't
        // subscribe to a ViewModel.
        postInvalidate()
    }

    companion object {
        private const val PERSISTED_VIEW_ID_KEY = "mvrx:persisted_view_id"
    }
}