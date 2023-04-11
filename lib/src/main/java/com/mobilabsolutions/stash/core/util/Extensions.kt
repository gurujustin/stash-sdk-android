/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import org.threeten.bp.LocalDate

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
fun LocalDate.withLastDayOfMonth(): LocalDate {
    return LocalDate.of(year, month, month.length(isLeapYear))
}

fun EditText.showKeyboard() {
    postDelayed({
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }, 500)
}

fun Activity.hideSoftInput() {
    val imm: InputMethodManager? = ContextCompat.getSystemService(this, InputMethodManager::class.java)
    val currentFocus = currentFocus
    if (currentFocus != null && imm != null) {
        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}