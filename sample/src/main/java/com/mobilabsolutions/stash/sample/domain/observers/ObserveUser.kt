package com.mobilabsolutions.stash.sample.domain.observers

import com.mobilabsolutions.stash.sample.data.entities.User
import com.mobilabsolutions.stash.sample.data.repositories.user.UserRepository
import com.mobilabsolutions.stash.sample.domain.SubjectInteractor
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-08-2019.
 */
class ObserveUser @Inject constructor(
    dispatchers: AppCoroutineDispatchers,
    private val userRepository: UserRepository
) : SubjectInteractor<Unit, User>() {
    override val dispatcher: CoroutineDispatcher = dispatchers.io

    override fun createObservable(params: Unit): Flow<User> {
        return userRepository.observerUser()
    }
}