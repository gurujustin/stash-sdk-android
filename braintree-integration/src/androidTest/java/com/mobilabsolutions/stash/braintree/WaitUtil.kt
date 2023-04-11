/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
fun waitFor(millis: Long): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isRoot()
        }

        override fun getDescription(): String {
            return "Wait for $millis milliseconds."
        }

        override fun perform(uiController: UiController, view: View) {
            uiController.loopMainThreadForAtLeast(millis)
        }
    }
}