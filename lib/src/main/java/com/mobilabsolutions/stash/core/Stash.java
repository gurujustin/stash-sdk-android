/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core;

import android.app.Application;

import com.mobilabsolutions.stash.core.internal.StashImpl;

/**
 * This is a main interface to Stash.
 * <p>
 * It is used by providing a configuration object to the initialize method, and retrieving {@link RegistrationManager}
 * instance to register your payment methods. You can also customize UI components by providing customization configuration.
 *
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
public final class Stash {


    /**
     * Initialize the SDK by using configuration object {@link StashConfiguration}. This needs to be done before the Stash is used
     * for the first time.
     *
     * @param applicationContext Application context
     * @param stashConfiguration SDK configuration object
     */
    public static void initialize(Application applicationContext, StashConfiguration stashConfiguration) {
        StashImpl.initialize(applicationContext, stashConfiguration);
    }

    /**
     * Provide a customization object for UI components so it blends better with the rest of your application.
     *
     * @param stashUIConfiguration ui configuration object
     */
    public static void configureUi(StashUiConfiguration stashUIConfiguration) {
        StashImpl.configureUi(stashUIConfiguration);
    }


    /**
     * Retrieve the instance of registration manager used to register various payment methods
     *
     * @return registration manager
     */
    public static RegistrationManager getRegistrationManager() {
        return StashImpl.getRegistrationManager();
    }

    public static boolean initialised(){
        return StashImpl.initialized();
    }

}
