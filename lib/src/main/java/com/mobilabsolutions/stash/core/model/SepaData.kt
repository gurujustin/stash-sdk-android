/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.model

import org.iban4j.Iban

/**
 * This class models the data needed to register sepa account as a payment method alias
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
data class SepaData(
    val bic: String? = null,
    val iban: String,
    val billingData: BillingData? = null

) {
    companion object {
        const val IBAN = "IBAN"
    }

    @Transient
    private var parsedIban: Iban = Iban.valueOf(iban)

    val accountNumberFromIban: String
        get() = parsedIban.accountNumber

    val bankCodeFromIban: String
        get() = parsedIban.bankCode

    val bankNumberFromIban: String
        get() = parsedIban.bban

    class Builder(iban: String) {
        var bic: String? = null
        var iban: String? = null
        var billingData: BillingData? = null
        var sepaData = SepaData(iban = iban)

        fun setBic(bic: String): Builder {
            this.bic = bic
            return this
        }

        fun setIban(iban: String): Builder {
            this.iban = iban
            return this
        }

        fun setBillingData(billingData: BillingData): Builder {
            this.billingData = billingData
            return this
        }

        fun build(): SepaData {
            return SepaData(
                bic,
                iban!!,
                billingData
            )
        }
    }
}
