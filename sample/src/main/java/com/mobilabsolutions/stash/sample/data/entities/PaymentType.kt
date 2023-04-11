/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.entities

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */

enum class PaymentType(val displayName: String) {
    CC("Credit Card"),
    SEPA("SEPA"),
    PAY_PAL("PayPal");

    companion object {
        fun fromStringValue(value: String): PaymentType? = values().firstOrNull { it.name == value }
    }
}
