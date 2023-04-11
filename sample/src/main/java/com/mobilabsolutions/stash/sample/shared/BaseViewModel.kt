/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.shared

import android.util.Log
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import com.mobilabsolutions.stash.sample.BuildConfig
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
open class BaseViewModel<S : MvRxState>(
    initialState: S
) : BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG) {

    val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected suspend inline fun <T> Flow<T>.execute(
        crossinline stateReducer: S.(Async<T>) -> S
    ) = execute({ it }, stateReducer)

    protected suspend inline fun <T, V> Flow<T>.execute(
        crossinline mapper: (T) -> V,
        crossinline stateReducer: S.(Async<V>) -> S
    ) {
        setState { stateReducer(Loading()) }

        @Suppress("USELESS_CAST")
        return map { Success(mapper(it)) as Async<V> }
            .catch {
                if (BuildConfig.DEBUG) {
                    Log.e(this::class.java.simpleName,
                        "Exception during observe", it)
                }
                emit(Fail(it))
            }
            .collect { setState { stateReducer(it) } }
    }

    protected val errorChannel = ConflatedBroadcastChannel<Throwable>()
    val errorMsg: Flow<Throwable> by lazy(LazyThreadSafetyMode.NONE) {
        errorChannel.asFlow()
    }
}