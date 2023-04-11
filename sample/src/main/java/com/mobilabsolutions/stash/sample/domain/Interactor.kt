package com.mobilabsolutions.stash.sample.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

abstract class Interactor<in P> {
    protected abstract val scope: CoroutineScope

    operator fun invoke(params: P, timeoutMs: Long = defaultTimeoutMs): Flow<InvokeStatus> {
        val channel = ConflatedBroadcastChannel<InvokeStatus>(InvokeIdle)
        scope.launch {
            try {
                withTimeout(timeoutMs) {
                    channel.send(InvokeStarted)
                    doWork(params)
                    channel.send(InvokeFinished)
                }
            } catch (t: TimeoutCancellationException) {
                channel.send(InvokeTimeout)
            } catch (error: Throwable) {
                channel.send(InvokeError(error))
            }
        }
        return channel.asFlow()
    }

    suspend fun executeSync(params: P) {
        scope.launch { doWork(params) }.join()
    }

    protected abstract suspend fun doWork(params: P)

    companion object {
        private val defaultTimeoutMs = TimeUnit.MINUTES.toMillis(5)
    }
}

interface ObservableInteractor<T> {
    val dispatcher: CoroutineDispatcher
    fun observe(): Flow<T>
}

abstract class SuspendingWorkInteractor<P : Any, T : Any> : ObservableInteractor<T> {
    private val channel = ConflatedBroadcastChannel<T>()

    suspend operator fun invoke(params: P) = channel.send(doWork(params))

    abstract suspend fun doWork(params: P): T

    override fun observe(): Flow<T> = channel.asFlow().distinctUntilChanged()
}

abstract class SubjectInteractor<P : Any, T> : ObservableInteractor<T> {
    private val channel = ConflatedBroadcastChannel<P>()

    operator fun invoke(params: P) = channel.sendBlocking(params)

    protected abstract fun createObservable(params: P): Flow<T>

    override fun observe(): Flow<T> = channel.asFlow()
        .distinctUntilChanged()
        .flatMapLatest { createObservable(it) }
}

operator fun Interactor<Unit>.invoke() = invoke(Unit)
operator fun <T> SubjectInteractor<Unit, T>.invoke() = invoke(Unit)

fun <I : ObservableInteractor<T>, T> CoroutineScope.launchObserve(
    interactor: I,
    f: suspend (Flow<T>) -> Unit
) {
    launch(interactor.dispatcher) {
        f(interactor.observe())
    }
}

sealed class InvokeStatus
object InvokeIdle : InvokeStatus()
object InvokeStarted : InvokeStatus()
object InvokeFinished : InvokeStatus()
object InvokeTimeout : InvokeStatus()
data class InvokeError(val throwable: Throwable) : InvokeStatus()