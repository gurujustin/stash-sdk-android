/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.extensions

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
fun priceWithCurrencyString(price: Int): String {
    // default is EURO
    val defaultCurrency = "EUR"
    val numberFormat = NumberFormat.getCurrencyInstance()
    numberFormat.currency = Currency.getInstance(defaultCurrency)
    var amount: BigDecimal
    if (price > 0) {
        amount = BigDecimal(price)
        amount = amount.movePointLeft(2)
    } else {
        amount = BigDecimal.ZERO
    }
    return numberFormat.format(amount.toDouble())
}