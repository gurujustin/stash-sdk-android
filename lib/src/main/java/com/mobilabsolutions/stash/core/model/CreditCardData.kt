/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.model

/**
 * This class models data needed to register a credit card as a payment method alias
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
data class CreditCardData(
    val number: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    /**
     * Card security code (CSC; also called card verification data - CVD,
     * card verification number, card verification value - CVV, card verification value code,
     * card verification code - CVC, verification code - V-code or V code], or signature panel code - SPC
     */
    val cvv: String,
    val billingData: BillingData? = null
) {
    companion object {

        const val CREDIT_CARD_NUMBER = "CREDIT_CARD_NUMBER"
        const val EXPIRY_MONTH = "EXPIRY_DATE"
        const val EXPIRY_YEAR = "EXPIRY_YEAR"
        const val CVV = "CVV"
    }

    class Builder {
        private var number: String? = null
        private var expiryMonth: Int? = null
        private var expiryYear: Int? = null
        private var cvv: String? = null
        private var billingData: BillingData? = null

        fun setNumber(number: String): Builder {
            this.number = number
            return this
        }
        fun setExpiryMonth(expiryMonth: Int): Builder {
            this.expiryMonth = expiryMonth
            return this
        }
        fun setExpiryYear(expiryYear: Int): Builder {
            this.expiryYear = expiryYear
            return this
        }

        /**
         * Set card security code (CSC; also called card verification data - CVD,
         * card verification number, card verification value - CVV, card verification value code,
         * card verification code - CVC, verification code - V-code or V code], or signature panel code - SPC
         */
        fun setCvv(cvv: String): Builder {
            this.cvv = cvv
            return this
        }
        fun setBillingData(billingData: BillingData): Builder {
            this.billingData = billingData
            return this
        }

        fun build(): CreditCardData {
            return CreditCardData(
                number!!,
                expiryMonth!!,
                expiryYear!!,
                cvv!!,
                billingData!!)
        }
    }
}
