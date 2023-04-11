/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.model

import com.mobilabsolutions.stash.core.exceptions.base.ValidationException
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BillingDataTest {

    @Test
    fun testNameHandling() {
        assertTrue {
            val billingData = BillingData(firstName = "FirstName", lastName = "LastName")
            billingData.fullName() == "FirstName LastName"
        }

        assertTrue {
            val billingData = BillingData(firstName = "FirstName")
            billingData.fullName() == "FirstName"
        }
        assertTrue {
            val billingData = BillingData(lastName = "LastName")
            billingData.fullName() == "LastName"
        }

        assertFailsWith(ValidationException::class) {
            val billingData = BillingData()
            billingData.fullName()
        }
    }

    @Test
    fun testBuilder() {
        val constructorCreated = BillingData(
            firstName = "Test First Name",
            lastName = "Test Last Name",
            email = "Test Email",
            address1 = "Test Address",
            address2 = "Test Address 2",
            zip = "Test Zip",
            city = "Test City",
            country = "Test Country",
            languageId = "Test Language"
        )

        val builderCreated = BillingData.Builder()
            .setFirstName("Test First Name")
            .setLastName("Test Last Name")
            .setEmail("Test Email")
            .setAddress1("Test Address")
            .setAddress2("Test Address 2")
            .setZip("Test Zip")
            .setCity("Test City")
            .setCountry("Test Country")
            .setLanguageId("Test Language")
            .build()

        assertTrue { constructorCreated == builderCreated }
    }
}