/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.uicomponents

import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.DrawableRes
import com.mobilabsolutions.stash.core.R
import com.mobilabsolutions.stash.core.CreditCardTypeWithRegex

class CardNumberTextWatcher(val cardIconChanged: (Int) -> Unit) : TextWatcher {

    // It can be a space or a hyphen
    private var delimiter: String = DEFAULT_DELIMITER

    private var changeLocation: Int = 0

    // Default to Visa, as most cards falls in this number grouping pattern
    private var groupingPattern: String = GroupingPattern.VISA_MASTER.pattern

    override fun beforeTextChanged(sequence: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(sequence: CharSequence, start: Int, before: Int, count: Int) {
        changeLocation = start
    }

    override fun afterTextChanged(editable: Editable) {
        if (changeLocation <= 2 && editable.toString().length <= 2) {
            // Reset if user clears the entry and try to enter a new card
            cardIconChanged(R.drawable.ic_card_default)
        } else if (changeLocation <= 7 || changeLocation >= 16) {
            // Mostly we can identify the card type by first 4 digits, except for China UnionPay , 17, for 19 digit cards)
            identify(editable)
        }
        applyPattern(editable)
    }

    private fun identify(editable: Editable) {
        val currentString = editable.toString().getCardNumberStringUnformatted()
        for (matchingPattern in CardTypeWithIcon.values()) {
            if (currentString.matches(matchingPattern.cardTypeWithRegex.regex)) {
                groupingPattern = (when (matchingPattern) {
                    CardTypeWithIcon.AMEX -> GroupingPattern.AMEX.pattern
                    CardTypeWithIcon.DINERS -> GroupingPattern.DINERS.pattern
                    CardTypeWithIcon.MAESTRO_13 -> GroupingPattern.MAESTRO_13.pattern
                    CardTypeWithIcon.MAESTRO_15 -> GroupingPattern.MAESTRO_15.pattern
                    CardTypeWithIcon.UNIONPAY_19 -> GroupingPattern.UNIONPAY_19.pattern
                    else -> GroupingPattern.VISA_MASTER.pattern
                })
                cardIconChanged(matchingPattern.resource)
                break
            }
        }
    }

    private fun applyPattern(editable: Editable) {
        val currentString = editable.toString()

        // Remove old grouping & apply new grouping, this covers any editing in between
        var processedString = editable.toString().getCardNumberStringUnformatted()

        Regex(groupingPattern).find(processedString)?.let {
            processedString = it.destructured.toList().joinToString(separator = delimiter).trim()

            // It's costly, don't apply it if they match
            if (currentString != processedString) {
                editable.replace(0, currentString.length, processedString)
            }
        }
    }

    companion object {

        const val DEFAULT_DELIMITER = " "

        @Suppress("UNUSED")
        const val HYPHEN_DELIMITER = "-"
    }

    /**
     * Ref : https://baymard.com/checkout-usability/credit-card-patterns
     */
    enum class GroupingPattern(val pattern: String) {

        VISA_MASTER("(\\d{4})(\\d{0,4})(\\d{0,4})(\\d{0,4})(\\d{0,3})"), // 4-4-4-4(-3)

        AMEX("(\\d{4})(\\d{0,6})(\\d{0,5})"), // 4-6-5

        DINERS("(\\d{4})(\\d{0,6})(\\d{0,4})"), // 4-6-4

        MAESTRO_13("(\\d{4})(\\d{0,4})(\\d{0,5})"), // 4-4-5

        MAESTRO_15("(\\d{4})(\\d{0,6})(\\d{0,5})"), // 4-6-5

        UNIONPAY_19("(\\d{6})(\\d{0,13})") // 6-13
    }

    enum class CardTypeWithIcon(val cardTypeWithRegex: CreditCardTypeWithRegex, @DrawableRes val resource: Int) {

        JCB(CreditCardTypeWithRegex.JCB, R.drawable.ic_card_jcb),

        AMEX(CreditCardTypeWithRegex.AMEX, R.drawable.ic_card_amex),

        DINERS(CreditCardTypeWithRegex.DINERS, R.drawable.ic_card_diners),

        VISA(CreditCardTypeWithRegex.VISA, R.drawable.ic_card_visa),

        MAESTRO_13(CreditCardTypeWithRegex.MAESTRO_13, R.drawable.ic_card_maestro),

        MAESTRO_15(CreditCardTypeWithRegex.MAESTRO_15, R.drawable.ic_card_maestro),

        MASTER_CARD(CreditCardTypeWithRegex.MASTER_CARD, R.drawable.ic_card_master),

        // Conflicts with MASTERCARD, need a way (number range) to distinguish. Also, applicable only for US Cards
        // DINERS_US(Regex("^5[45][0-9]{1,14}$"), R.drawable.ic_card_diners),

        DISCOVER(CreditCardTypeWithRegex.DISCOVER, R.drawable.ic_card_discover),

        UNIONPAY_16(CreditCardTypeWithRegex.UNIONPAY_16, R.drawable.ic_card_unionpay),

        UNIONPAY_19(CreditCardTypeWithRegex.UNIONPAY_19, R.drawable.ic_card_unionpay),

        MAESTRO(CreditCardTypeWithRegex.MAESTRO, R.drawable.ic_card_maestro)
    }
}