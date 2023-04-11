/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.uicomponents

import android.text.Editable
import android.text.TextWatcher

class IbanTextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Do Nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Do Nothing
    }

    override fun afterTextChanged(editable: Editable) {
        val currentString = editable.toString()

        // Remove old grouping & apply new grouping, this covers any editing in between
        val processedString = editable
            .toString()
            .getIbanStringUnformatted()
            .replace("(\\S{4})(?=\\d)".toRegex(), "$1 ")

        // It's costly, don't apply it if they match
        if (currentString != processedString) {
            editable.replace(0, currentString.length, processedString)
        }
    }
}