/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core

/**
 * Represents the payment method types the SDK currently supports
 */
enum class PaymentMethodType {
    /**
     * Credit card
     */
    CC,
    /**
     * SEPA bank account
     */
    SEPA,
    /**
     * PayPal account
     */
    PAYPAL
}