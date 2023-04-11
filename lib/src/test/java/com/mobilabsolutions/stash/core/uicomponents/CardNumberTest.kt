/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.uicomponents

import com.mobilabsolutions.stash.core.internal.uicomponents.CardNumberTextWatcher.CardTypeWithIcon
import com.mobilabsolutions.stash.core.internal.uicomponents.CardNumberTextWatcher.GroupingPattern
import org.junit.Assert.assertEquals
import org.junit.Test

class CardNumberTest {

    //region Grouping Tests
    private fun applyPattern(pattern: String, testString: String): String? {
        return Regex(pattern).find(testString)?.destructured?.toList()?.joinToString(separator = " ")?.trim()
    }

    @Test
    fun testVisaGroupingFull() {
        val testCard = "4111111111111111"
        // 4-4-4-4(-3)
        assertEquals(
                applyPattern(GroupingPattern.VISA_MASTER.pattern, testCard),
                "4111 1111 1111 1111"
        )
    }

    @Test
    fun testVisaGroupingWith5digits() {
        val testCard = "41111"
        // 4-4-4-4(-3)
        assertEquals(
                applyPattern(GroupingPattern.VISA_MASTER.pattern, testCard),
                "4111 1"
        )
    }

    @Test
    fun testVisaGroupingWith9digits() {
        val testCard = "411111111"
        // 4-4-4-4(-3)
        assertEquals(
                applyPattern(GroupingPattern.VISA_MASTER.pattern, testCard),
                "4111 1111 1"
        )
    }

    @Test
    fun testVisaGroupingWith13digits() {
        val testCard = "4111111111111"
        // 4-4-4-4(-3)
        assertEquals(
                applyPattern(GroupingPattern.VISA_MASTER.pattern, testCard),
                "4111 1111 1111 1"
        )
    }

    @Test
    fun testAmexGroupingFull() {
        val testCard = "340000000000000"
        // 4-6-5
        assertEquals(
                applyPattern(GroupingPattern.AMEX.pattern, testCard),
                "3400 000000 00000"
        )
    }

    @Test
    fun testAmexGroupingWith5digits() {
        val testCard = "34000"
        // 4-6-5
        assertEquals(
                applyPattern(GroupingPattern.AMEX.pattern, testCard),
                "3400 0"
        )
    }

    @Test
    fun testAmexGrouping11digits() {
        val testCard = "34000000000"
        // 4-6-5
        assertEquals(
                applyPattern(GroupingPattern.AMEX.pattern, testCard),
                "3400 000000 0"
        )
    }

    @Test
    fun testDinersGroupingFull() {
        val testCard = "36000000000000"
        // 4-6-4
        assertEquals(
                applyPattern(GroupingPattern.DINERS.pattern, testCard),
                "3600 000000 0000"
        )
    }

    @Test
    fun testDinersGroupingWith5digits() {
        val testCard = "36000"
        // 4-6-4
        assertEquals(
                applyPattern(GroupingPattern.DINERS.pattern, testCard),
                "3600 0"
        )
    }

    @Test
    fun testDinersGrouping11digits() {
        val testCard = "36000000000"
        // 4-6-4
        assertEquals(
                applyPattern(GroupingPattern.DINERS.pattern, testCard),
                "3600 000000 0"
        )
    }

    @Test
    fun testMaestro13GroupingFull() {
        val testCard = "5000000000000"
        // 4-4-5
        assertEquals(
                applyPattern(GroupingPattern.MAESTRO_13.pattern, testCard),
                "5000 0000 00000"
        )
    }

    @Test
    fun testMaestro13GroupingWith5digits() {
        val testCard = "50000"
        // 4-4-5
        assertEquals(
                applyPattern(GroupingPattern.MAESTRO_13.pattern, testCard),
                "5000 0"
        )
    }

    @Test
    fun testMaestro13GroupingWith9digits() {
        val testCard = "500000000"
        // 4-4-5
        assertEquals(
                applyPattern(GroupingPattern.MAESTRO_13.pattern, testCard),
                "5000 0000 0"
        )
    }

    @Test
    fun testMaestro15GroupingFull() {
        val testCard = "500000000000000"
        // 4-6-5
        assertEquals(
                applyPattern(GroupingPattern.MAESTRO_15.pattern, testCard),
                "5000 000000 00000"
        )
    }

    @Test
    fun testMaestro15GroupingWith5digits() {
        val testCard = "50000"
        // 4-6-5
        assertEquals(
                applyPattern(GroupingPattern.MAESTRO_15.pattern, testCard),
                "5000 0"
        )
    }

    @Test
    fun testMaestro15GroupingWith11Digits() {
        val testCard = "50000000000"
        // 4-6-5
        assertEquals(
                applyPattern(GroupingPattern.MAESTRO_15.pattern, testCard),
                "5000 000000 0"
        )
    }

    @Test
    fun testUnionPay19GroupingFull() {
        val testCard = "6200000000000000000"
        // 6-13
        assertEquals(
                applyPattern(GroupingPattern.UNIONPAY_19.pattern, testCard),
                "620000 0000000000000"
        )
    }

    @Test
    fun testUnionPay19GroupingWith7Digits() {
        val testCard = "6200000"
        // 6-13
        assertEquals(
                applyPattern(GroupingPattern.UNIONPAY_19.pattern, testCard),
                "620000 0"
        )
    }
    //endregion

    //region Matching Tests
    private fun checkPattern(testString: String): CardTypeWithIcon? {
        for (matchingPattern in CardTypeWithIcon.values()) {
            if (testString.matches(matchingPattern.cardTypeWithRegex.regex)) {
                return matchingPattern
            }
        }
        return null
    }

    @Test
    fun testNoneMatchingEmpty() {
        val testCard = ""
        assertEquals(
                checkPattern(testCard),
                null
        )
    }

    @Test
    fun testNoneMatchingFull() {
        val testCard = "0000000000000000000"
        assertEquals(
                checkPattern(testCard),
                null
        )
    }

    @Test
    fun testJcbMatchingFull() {
        val testCard = "1800000000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.JCB
        )
    }

    @Test
    fun testAmexMatchingFull() {
        val testCard = "340000000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.AMEX
        )
    }

    @Test
    fun testDinersMatchingFull() {
        val testCard = "36000000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.DINERS
        )
    }

    @Test
    fun testVisaMatchingFull() {
        val testCard = "4111111111111111"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.VISA
        )
    }

    @Test
    fun testMaestro13MatchingFull() {
        val testCard = "5011000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.MAESTRO_13
        )
    }

    @Test
    fun testMaestro15MatchingFull() {
        val testCard = "560000000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.MAESTRO_15
        )
    }

    @Test
    fun testMaestroMatchingFull() {
        val testCard = "6111000000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.MAESTRO
        )
    }

    @Test
    fun testMasterCardMatchingFull() {
        val testCard = "5111000000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.MASTER_CARD
        )
    }

    @Test
    fun testDiscoverMatchingFull() {
        val testCard = "6011000000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.DISCOVER
        )
    }

    @Test
    fun testUnionPay16MatchingFull() {
        val testCard = "6200000000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.UNIONPAY_16
        )
    }

    @Test
    fun testUnionPay19MatchingFull() {
        val testCard = "6200000000000000000"
        assertEquals(
                checkPattern(testCard),
                CardTypeWithIcon.UNIONPAY_19
        )
    }
    //endregion
}