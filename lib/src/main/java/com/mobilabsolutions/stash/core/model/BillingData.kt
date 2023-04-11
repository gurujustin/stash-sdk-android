/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.model

import com.mobilabsolutions.stash.core.exceptions.base.ValidationException
import java.util.Locale

/**
 * This class models the billing data needed when registering
 * a credit card or a sepa direct debit method as a payment aliasId
 *
 * @author [Ugi](ugi@mobilabsolutions.com)
 */

data class BillingData(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var address1: String? = null,
    var address2: String? = null,
    var zip: String? = null,
    var city: String? = null,
    var country: String? = null,
    val languageId: String = Locale.getDefault().isO3Language
) {

    companion object {

        const val ADDITIONAL_DATA_FIRST_NAME = "ADDITIONAL_DATA_FIRST_NAME"

        const val ADDITIONAL_DATA_LAST_NAME = "ADDITIONAL_DATA_LAST_NAME"

        const val ADDITIONAL_DATA_COUNTRY = "ADDITIONAL_DATA_COUNTRY"

        const val ADDITIONAL_DATA_EMAIL = "ADDITONAL_DATA_EMAIL"
    }

    class Builder {
        var billingData = BillingData()

        fun setFirstName(firstName: String): Builder {
            billingData = billingData.copy(firstName = firstName)
            return this
        }

        fun setLastName(lastName: String): Builder {
            billingData = billingData.copy(lastName = lastName)
            return this
        }

        fun setEmail(email: String): Builder {
            billingData = billingData.copy(email = email)
            return this
        }

        fun setAddress1(address1: String): Builder {
            billingData = billingData.copy(address1 = address1)
            return this
        }

        fun setAddress2(address2: String): Builder {
            billingData = billingData.copy(address2 = address2)
            return this
        }

        fun setZip(zip: String): Builder {
            billingData = billingData.copy(zip = zip)
            return this
        }

        fun setCity(city: String): Builder {
            billingData = billingData.copy(city = city)
            return this
        }

        fun setCountry(country: String): Builder {
            billingData = billingData.copy(country = country)
            return this
        }

        fun setLanguageId(languageId: String): Builder {
            billingData = billingData.copy(languageId = languageId)
            return this
        }

        fun build() = billingData
    }

    fun fullName(): String {
        return when {
            firstName != null && lastName != null -> "$firstName $lastName"
            firstName != null -> firstName
            lastName != null -> lastName
            else -> throw ValidationException("First name and last name were not supplied")
        }!!
    }
}
