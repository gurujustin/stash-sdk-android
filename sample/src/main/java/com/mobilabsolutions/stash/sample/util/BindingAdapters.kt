/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mobilabsolutions.stash.sample.extensions.priceWithCurrencyString

object BindingAdapters {
    // private val format: NumberFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY)

    @JvmStatic
    @BindingAdapter("currency")
    fun TextView.bindCurrency(price: Int) {
        // text = format.format(price / 100)
        text = priceWithCurrencyString(price)
    }

    @JvmStatic
    @BindingAdapter("image")
    fun ImageView.setImage(name: String) {
        setImageResource(context.resources.getIdentifier("drawable/$name", null, context.packageName))
    }
}