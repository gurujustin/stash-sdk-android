/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.uicomponents

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.os.Bundle
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.mobilabsolutions.stash.core.CustomizationExtensions
import com.mobilabsolutions.stash.core.R
import com.mobilabsolutions.stash.core.StashUiConfiguration
import kotlinx.android.synthetic.main.month_year_picker.*
import org.threeten.bp.LocalDate

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class MonthYearPicker(
    context: Context,
    themeResId: Int = R.style.MonthYearPickerStyle,
    cancellable: Boolean = true,
    onCancelListener: DialogInterface.OnCancelListener? = null,
    val stashUIConfiguration: StashUiConfiguration? = null,
    val selectedDate: LocalDate? = null,
    val onDatePickedListener: (Pair<Int, Int>) -> Unit
) : Dialog(context, themeResId) {

    init {
        setCancelable(cancellable)
        setOnCancelListener(onCancelListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val today = LocalDate.now()
        setContentView(R.layout.month_year_picker)
        monthNumberPicker.minValue = today.monthValue
        monthNumberPicker.maxValue = 12
        yearNumberPicker.minValue = today.year
        yearNumberPicker.maxValue = today.year + 20

        yearNumberPicker.wrapSelectorWheel = false

        stashUIConfiguration?.apply {
            monthYearPickerRoot.background = context.getDrawable(backgroundColor)
            CustomizationExtensions {
                okButton.applyCustomization(this@apply)
                monthTitle.applyTextCustomization(this@apply)
                yearTitle.applyTextCustomization(this@apply)
            }
            try {
                val selectorWheelPaintField = NumberPicker::class.java
                        .getDeclaredField("mSelectorWheelPaint")
                selectorWheelPaintField.isAccessible = true
                val color = ContextCompat.getColor(context, stashUIConfiguration.textColor)
                (selectorWheelPaintField.get(monthNumberPicker) as Paint).color = color
                (selectorWheelPaintField.get(yearNumberPicker) as Paint).color = color

                var count = monthNumberPicker.childCount
                for (i in 0 until count) {
                    val child = monthNumberPicker.getChildAt(i)
                    if (child is EditText)
                        child.setTextColor(color)
                }
                selectedDate?.let {
                    monthNumberPicker.value = it.monthValue
                }
                monthNumberPicker.invalidate()

                count = yearNumberPicker.childCount
                for (i in 0 until count) {
                    val child = yearNumberPicker.getChildAt(i)
                    if (child is EditText)
                        child.setTextColor(color)
                }
                selectedDate?.let {
                    yearNumberPicker.value = it.year
                }
                yearNumberPicker.invalidate()
            } catch (e: Exception) {
                // Just ignore applying customizations if any throwable happens
            }
        }

        yearNumberPicker.setOnValueChangedListener { _, _, newVal ->
            if (newVal > today.year) {
                monthNumberPicker.minValue = 1
            } else {
                monthNumberPicker.minValue = today.monthValue
            }
        }

        okButton.setOnClickListener {
            onDatePickedListener.invoke(
                    Pair(monthNumberPicker.value, yearNumberPicker.value)
            )
            dismiss()
        }
    }
}