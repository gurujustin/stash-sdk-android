/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.gson.Gson
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */

/**
 * A class representing a UI customization preferences. Default values are provided,
 * or you can use the Builder supplied. The values must be reference to the color
 * defined in application resources.
 */
data class StashUiConfiguration(
    /**
     * Text color
     */
    @ColorRes val textColor: Int = R.color.gable_green,
    /**
     * Background color
     */
    @ColorRes val backgroundColor: Int = R.color.black_haze,
    /**
     * Button color
     */
    @ColorRes val buttonColor: Int = R.color.lochmara,
    /**
     * Button text color
     */
    @ColorRes val buttonTextColor: Int = R.color.white,
    /**
     * Background color of box containing entry edit text views
     */
    @ColorRes val cellBackgroundColor: Int = R.color.white,
    /**
     * Color of the text inside edit text fields
     */
    @ColorRes val mediumEmphasisColor: Int = R.color.white,
    /**
     * Background color of error snackBar
     */
    @ColorRes val snackBarBackground: Int = R.color.carnation

) {
    class Builder {
        private var preference = StashUiConfiguration()
        /**
         * Text color
         */
        fun setTextColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(textColor = resourceId)
            return this
        }

        /**
         * Background color
         */
        fun setBackgroundColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(backgroundColor = resourceId)
            return this
        }

        /**
         * Button color
         */
        fun setButtonColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(buttonColor = resourceId)
            return this
        }

        /**
         * Button text color
         */
        fun setButtonTextColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(buttonTextColor = resourceId)
            return this
        }

        /**
         * Background color of box containing entry edit text views
         */
        fun setCellBackgroundColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(cellBackgroundColor = resourceId)
            return this
        }

        /**
         * Color of the text inside edit text fields
         */
        fun setMediumEmphasisColor(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(mediumEmphasisColor = resourceId)
            return this
        }

        /**
         * Background color of the error snackBar
         */
        fun setSnackBarBackground(@ColorRes resourceId: Int): Builder {
            preference = preference.copy(snackBarBackground = resourceId)
            return this
        }

        fun build(): StashUiConfiguration = preference
    }
}

/**
 * UI customization manager allows you to customize Credit card entry screens by selecting specific
 * colors for specific elements
 */
class UiCustomizationManager @Inject internal constructor(val gson: Gson, val sharedPreferences: SharedPreferences) {
    private lateinit var stashUIConfiguration: StashUiConfiguration

    companion object {
        const val CUSTOMIZATION_KEY = "Customization"
    }

    init {
        loadPreference()
    }

    fun setCustomizationPreferences(stashUIConfiguration: StashUiConfiguration) {
        this.stashUIConfiguration = stashUIConfiguration
        storePreference()
    }

    fun getCustomizationPreferences(): StashUiConfiguration {
        return stashUIConfiguration
    }

    @SuppressLint("ApplySharedPref")
    private fun storePreference() {
        val preferenceJson = gson.toJson(stashUIConfiguration)
        sharedPreferences.edit().putString(CUSTOMIZATION_KEY, preferenceJson).commit()
    }

    private fun loadPreference() {
        val customizationJson = sharedPreferences.getString(CUSTOMIZATION_KEY, "")!!
        stashUIConfiguration = if (customizationJson.isEmpty()) {
            StashUiConfiguration()
        } else {
            gson.fromJson(customizationJson, StashUiConfiguration::class.java)
        }
    }
}

/**
 * We provide customizations inside an extension object, so we can limit the visibility of extension
 * functions throughout the project. This way we prevent poisoning the namespace.
 */
object CustomizationExtensions {

    operator fun invoke(body: CustomizationExtensions.() -> Unit): Unit = body.invoke(this)

    object CustomizationUtil {

        fun darken(color: Int): Int {
            return ColorUtils.blendARGB(color, Color.BLACK, 0.5f)
        }

        fun lighten(color: Int): Int {
            return ColorUtils.blendARGB(color, Color.WHITE, 0.2f)
        }
    }

    /**
     * Apply customizations on any edit text view
     */
    fun EditText.applyEditTextCustomization(stashUIConfiguration: StashUiConfiguration) {
        applyOnTextView(stashUIConfiguration)
    }

    /**
     * Make the text view look like customized edit text. This is useful for fields that
     * actually open dialogs like expiry date and country selector
     */
    fun TextView.applyFakeEditTextCustomization(stashUIConfiguration: StashUiConfiguration) {
        applyOnTextView(stashUIConfiguration)
    }

    /**
     * Apply text view background customizations
     */
    private fun TextView.applyOnTextView(stashUIConfiguration: StashUiConfiguration) {
        background = if (error == null) {
            val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.edit_text_selector)
            val textFieldDrawableContainerState = (backgroundDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
            val textFieldDrawableStates = textFieldDrawableContainerState.children.filterNotNull().map { it as GradientDrawable }
            textFieldDrawableStates[0].setStroke(1.px, CustomizationExtensions.CustomizationUtil.darken(ContextCompat.getColor(context, R.color.cool_gray)))
            textFieldDrawableStates[1].setStroke(1.px, ContextCompat.getColor(context, R.color.cool_gray))
            textFieldDrawableStates[0].setColor(ContextCompat.getColor(context, stashUIConfiguration.mediumEmphasisColor))
            textFieldDrawableStates[1].setColor(ContextCompat.getColor(context, stashUIConfiguration.mediumEmphasisColor))
            backgroundDrawable
        } else {
            ContextCompat.getDrawable(context, R.drawable.edit_text_frame_error)
        }
        this.applyTextCustomization(stashUIConfiguration)
    }

    /**
     * Customize text color
     */
    fun TextView.applyTextCustomization(stashUIConfiguration: StashUiConfiguration) {
        setTextColor(ContextCompat.getColor(context, stashUIConfiguration.textColor))
    }

    /**
     * Apply button customizations
     */
    fun Button.applyCustomization(stashUIConfiguration: StashUiConfiguration) {
        background = if (isEnabled) {
            val buttonColorDrawable = ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_selector)
            val buttonBackgroundDrawableContainterStates = (buttonColorDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
            val states = buttonBackgroundDrawableContainterStates.children.filterNotNull().map { it as GradientDrawable }
            states[0].setColor(CustomizationExtensions.CustomizationUtil.lighten(ContextCompat.getColor(context, stashUIConfiguration.buttonColor)))
            states[1].setColor(ContextCompat.getColor(context, stashUIConfiguration.buttonColor))
            buttonColorDrawable
        } else {
            val buttonColorDrawable = ContextCompat.getDrawable(context, R.drawable.rounded_corner_button_selector_disabled)
            val buttonBackgroundDrawableContainterStates = (buttonColorDrawable as StateListDrawable).constantState as DrawableContainer.DrawableContainerState
            val states = buttonBackgroundDrawableContainterStates.children.filterNotNull().map { it as GradientDrawable }
            states[0].setColor(ContextCompat.getColor(context, R.color.cool_gray))
            states[1].setColor(ContextCompat.getColor(context, R.color.cool_gray))
            buttonColorDrawable
        }
        setTextColor(CustomizationExtensions.CustomizationUtil.lighten(ContextCompat.getColor(context, stashUIConfiguration.buttonTextColor)))
    }

    /**
     * Apply background customization on any view that has ColorDrawable background
     */
    fun View.applyBackgroundCustomization(stashUIConfiguration: StashUiConfiguration) {
        val backgroundColorDrawable = background as ColorDrawable
        backgroundColorDrawable.color = ContextCompat.getColor(context, stashUIConfiguration.backgroundColor)
        background = backgroundColorDrawable
    }

    /**
     * Apply cell background customization on any view that has ColorDrawable background
     */
    fun View.applyCellBackgroundCustomization(stashUIConfiguration: StashUiConfiguration) {
        val backgroundColorDrawable = background as GradientDrawable
        backgroundColorDrawable.setColor(ContextCompat.getColor(context, stashUIConfiguration.cellBackgroundColor))
        background = backgroundColorDrawable
    }

    /**
     * Request focus for this view and show IME
     */
    fun View.showKeyboardAndFocus() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, 0)
    }
}

/**
 * Convert pixels to density pixels
 */
val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
/**
 * Convert density pixels to pixels
 */
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
