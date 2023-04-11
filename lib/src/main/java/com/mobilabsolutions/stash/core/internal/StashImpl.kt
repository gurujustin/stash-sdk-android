/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobilabsolutions.stash.core.BuildConfig
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.RegistrationManager
import com.mobilabsolutions.stash.core.StashConfiguration
import com.mobilabsolutions.stash.core.StashUiConfiguration
import com.mobilabsolutions.stash.core.UiCustomizationManager
import com.mobilabsolutions.stash.core.exceptions.base.ConfigurationException
import com.mobilabsolutions.stash.core.internal.psphandler.IntegrationCompanion
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import timber.log.Timber
import javax.inject.Inject
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

/**
 * This class is responsible for configuring and initializing the Stash. It is a singleton
 * that is injected with registration and customization managers after the dagger graph has been created.
 * Since we are providing the same dagger graph to the integration modules, graph initialization is a three
 * step process:
 *  * For each integration supplied by 3rd party developer using [IntegrationCompanion] we create a
 *  [IntegrationInitialization] object. This object serves as a reference to the concrete integration
 *  and will become part of the dagger graph to be injected where necessary (i.e. [PspCoordinator]
 *  * Once the dagger graph is created, it can then be given to all of the integration intializatiors
 *  so they can expand the graph by providing their own dependencies by calling [IntegrationInitialization.initialize]
 *  * Now the graph is complete, and a singleton instance of this class can be injected
 *
 *  Once injected this class provides Registration Manager to the rest of application and is reachable
 *  as a static reference.
 *
 *  Apart from initialization and configuration responsibilities this class also validates the configuration
 *  and throws appropriate exceptions in case of invalid configuration.
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class StashImpl(
    publishableKey: String,
    url: String,
    applicationContext: Application,
    integrationMap: Map<IntegrationCompanion, Set<PaymentMethodType>>,
    testMode: Boolean,
    sslSocketFactory: SSLSocketFactory?,
    x509TrustManager: X509TrustManager?
) {

    @Inject
    lateinit var newRegistrationManager: RegistrationManagerImpl

    @Inject
    internal lateinit var uiCustomizationManager: UiCustomizationManager

    val daggerGraph: StashComponent

    /**
     * Here we are building the dagger graph as described
     */
    init {

        val processedPaymentMethodTypes: MutableSet<PaymentMethodType> = mutableSetOf()
        integrationMap.forEach { (_, paymentMethodTypeSet) ->
            processedPaymentMethodTypes.forEach {
                if (paymentMethodTypeSet.contains(it)) {
                    throw ConfigurationException("Integration handling for ${it.name} payment method type already registered!")
                }
            }
            processedPaymentMethodTypes.addAll(paymentMethodTypeSet)
        }
        // We are creating initialization objects that can be delivered to the main graph
        val integrationInitializationMap = integrationMap.mapKeys { it.key.create(it.value) }

        // We are building modules, by providing configuration and created initialization objects
        daggerGraph = DaggerStashComponent.builder()
            .sslSupportModule(SslSupportModule(sslSocketFactory, x509TrustManager))
            .stashModule(StashModule(publishableKey, url, applicationContext, integrationInitializationMap, testMode))
            .build()

        // Now the graph is created and can be expanded by each of the initializations
        integrationInitializationMap.forEach { (initialization, _) ->
            initialization.initialize(daggerGraph)
        }
        // Finally we inject the dependencies
        daggerGraph.inject(this)
    }

    /**
     * Companion object holds the singleton instance, and verifies that the configuration is correct
     */
    companion object {

        private var instance: StashImpl? = null

        private var initialized = false

        private var stashComponent: StashComponent? = null

        /**
         * Initalize method verifies the configuration and invokes the creation of the singleton
         */
        @JvmStatic
        @Synchronized
        fun initialize(applicationContext: Application, configuration: StashConfiguration) {
            configuration.apply {

                if (initialized) {
                    throw ConfigurationException("Already initialized")
                }

                val integrationInitializationMap =
                    when {
                        integration == null && integrationList == null -> {
                            throw ConfigurationException("No integrations provided")
                        }
                        integration != null && integrationList != null -> {
                            throw ConfigurationException("Both integration and integration map were supplied," +
                                " provide only one or the other")
                        }
                        integration != null -> {
                            mapOf(integration to integration.supportedPaymentMethodTypes)
                        }
                        integrationList != null -> {
                            if (integrationList.isEmpty()) {
                                throw ConfigurationException("Integration list was provided, but it was empty")
                            }
                            integrationList.forEach {
                                // Check whether the paired payment method is supported
                                if (!it.first.supportedPaymentMethodTypes.contains(it.second)) {
                                    throw ConfigurationException("${it.second.name} is not supported in ${it.first.name} integration")
                                }
                            }
                            integrationList.toList().groupBy {
                                it.first
                            }.map {
                                Pair(it.key, it.value.map { pair -> pair.second }.toSet())
                            }.toMap()
                        }
                        else -> throw RuntimeException("This should never happen")
                    }
                if (BuildConfig.DEBUG) {
                    Timber.plant(Timber.DebugTree())
                }
                AndroidThreeTen.init(applicationContext)
                // Singleton instance creation
                instance = StashImpl(publishableKey, endpoint, applicationContext, integrationInitializationMap, testMode, sslFactory, x509TrustManager)

                initialized = true
                // Font customization
                ViewPump.init(ViewPump.builder()
                    .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Lato-Regular.ttf")
                            .build()))
                    .build())
                // UI customization
                if (configuration.stashUiConfiguration != null) {
                    configureUi(configuration.stashUiConfiguration)
                }
            }
        }

        /**
         * Update the UI configurations, effects will be applied only after the screen is recreated
         */
        @JvmStatic
        fun configureUi(stashUIConfiguration: StashUiConfiguration) {
            assertInitialized()
            instance!!.uiCustomizationManager.setCustomizationPreferences(stashUIConfiguration)
        }

        /**
         * Returns the registration manager object
         */
        @JvmStatic
        fun getRegistrationManager(): RegistrationManager {
            assertInitialized()
            return instance!!.newRegistrationManager
        }

        /**
         * Throw an exception if the sdk is not initialized
         */
        private fun assertInitialized() {
            if (instance == null) {
                throw RuntimeException(
                    "Stash is not initialized, make sure you called initialize method before using"
                )
            }
        }

        /**
         * Provide dagger graph to UI components that live in core library
         */
        internal fun getInjector(): StashComponent {
            return if (instance != null) {
                instance!!.daggerGraph
            } else {
                if (stashComponent != null) {
                    stashComponent!!
                } else {
                    throw RuntimeException("Stash not initialized!")
                }
            }
        }

        @JvmStatic
        fun initialized(): Boolean = initialized
    }
}