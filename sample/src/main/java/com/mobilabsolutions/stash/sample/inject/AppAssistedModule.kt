/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.inject

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
@AssistedModule
@Module(includes = [AssistedInject_AppAssistedModule::class])
abstract class AppAssistedModule