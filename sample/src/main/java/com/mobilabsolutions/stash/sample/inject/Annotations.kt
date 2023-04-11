package com.mobilabsolutions.stash.sample.inject

import javax.inject.Qualifier

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 23-08-2019.
 */

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@MustBeDocumented
annotation class ProcessLifetime

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
@MustBeDocumented
annotation class PerActivity