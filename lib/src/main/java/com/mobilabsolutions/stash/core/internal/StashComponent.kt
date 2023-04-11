/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal

import android.app.Application
import android.content.Context
import com.mobilabsolutions.stash.core.UiCustomizationManager
import com.mobilabsolutions.stash.core.internal.api.backend.MobilabApi
import com.mobilabsolutions.stash.core.internal.uicomponents.PaymentMethodChoiceFragment
import com.mobilabsolutions.stash.core.internal.uicomponents.RegistrationProcessHostActivity
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    SslSupportModule::class,
    StashModule::class
])
interface StashComponent {
    fun inject(stashImpl: StashImpl)

    fun inject(registrationProcessHostActivity: RegistrationProcessHostActivity)

    fun inject(paymentMethodChoiceFragment: PaymentMethodChoiceFragment)

    fun provideApplication(): Application

    fun provideMobilabApi(): MobilabApi

    fun providesContext(): Context

    fun provideRxJava2Converter(): RxJava2CallAdapterFactory

    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor

    fun provideUiCustomizationManager(): UiCustomizationManager
}