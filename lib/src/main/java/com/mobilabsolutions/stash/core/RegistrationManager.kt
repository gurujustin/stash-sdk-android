/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core

import android.app.Activity
import com.mobilabsolutions.stash.core.model.CreditCardData
import com.mobilabsolutions.stash.core.model.SepaData
import io.reactivex.Single
import java.util.UUID

/**
 * @author [Ugi](ugi@mobilabsolutions.com)
 */
interface RegistrationManager {

    /**
     * Register a credit card so you can use payment aliasId for future payments
     * @param creditCardData credit card information
     * @return string representing payment aliasId
     */
    fun registerCreditCard(
        activity: Activity,
        creditCardData: CreditCardData,
        idempotencyKey: UUID? = null
    ): Single<PaymentMethodAlias>

    /**
     * Register a sepa debit account so you can use payment aliasId for future payments
     * @param sepaData sepa card information
     * @return string representing payment aliasId
     */
    fun registerSepaAccount(
        activity: Activity,
        sepaData: SepaData,
        idempotencyKey: UUID? = null
    ): Single<PaymentMethodAlias>

    /**
     * Returns a list of supported payment methods
     * @return list of supported payment methods
     */
    fun getAvailablePaymentMethodsTypes(): Set<PaymentMethodType>

    /**
     * Let Stash handle data using built-in UI components
     *
     * @param activity the activity context to launch from. If activity is null, a new task will be created
     * @param specificPaymentMethodType skip payment method chooser and immediately show specific type entry UI
     * @returnstring string representing aliasId
     */
    fun registerPaymentMethodUsingUi(activity: Activity, specificPaymentMethodType: PaymentMethodType? = null): Single<PaymentMethodAlias>
}

data class PaymentMethodAlias(
    val alias: String,
    val paymentMethodType: PaymentMethodType,
    val extraAliasInfo: ExtraAliasInfo
) {
    fun getJavaExtraInfo(): ExtraAliasInfo.JavaExtraInfo {
        extraAliasInfo.apply {
            return when (this) {
                is ExtraAliasInfo.CreditCardExtraInfo -> ExtraAliasInfo.JavaExtraInfo(creditCardExtraInfo = this)
                is ExtraAliasInfo.SepaExtraInfo -> ExtraAliasInfo.JavaExtraInfo(sepaExtraInfo = this)
                is ExtraAliasInfo.PaypalExtraInfo -> ExtraAliasInfo.JavaExtraInfo(paypalExtraInfo = this)
            }
        }
    }
}

sealed class ExtraAliasInfo {

    data class CreditCardExtraInfo(
        val creditCardMask: String,
        val expiryMonth: Int,
        val expiryYear: Int,
        val creditCardType: CreditCardType
    ) : ExtraAliasInfo()

    data class SepaExtraInfo(
        val maskedIban: String
    ) : ExtraAliasInfo()

    data class PaypalExtraInfo(
        val email: String
    ) : ExtraAliasInfo()

    data class JavaExtraInfo(
        val creditCardExtraInfo: CreditCardExtraInfo? = null,
        val sepaExtraInfo: SepaExtraInfo? = null,
        val paypalExtraInfo: PaypalExtraInfo? = null
    )
}

enum class CreditCardType {
    JCB,
    AMEX,
    DINERS,
    VISA,
    MASTERCARD,
    DISCOVER,
    UNIONPAY,
    MAESTRO,
    UNKNOWN;

    companion object {
        fun fromStringValue(value: String): CreditCardType? = values().firstOrNull { it.name == value }
    }
}

enum class CreditCardTypeWithRegex(val regex: Regex) {
    JCB(Regex("^(?:2131|1800|35[0-9]{3})[0-9]{3,}$")),
    AMEX(Regex("^3[47][0-9]{1,13}$")),
    DINERS(Regex("^3(?:0[0-5]|[68][0-9])[0-9]{2,}$")),
    VISA(Regex("^4[0-9]{2,12}(?:[0-9]{3})?$")),
    MAESTRO_13(Regex("^50[0-9]{1,11}$")),
    MAESTRO_15(Regex("^5[68][0-9]{1,13}$")),
    MASTER_CARD(Regex("^5[1-5][0-9]{1,14}$")),
    DISCOVER(Regex("^6(?:011|5[0-9]{2})[0-9]{3,}$")),
    UNIONPAY_16(Regex("^62[0-9]{1,14}$")),
    UNIONPAY_19(Regex("^62[0-9]{15,17}$")),
    MAESTRO(Regex("^6[0-9]{1,18}$")),
    UNKNOWN(Regex(""));

    companion object {
        fun resolveCreditCardType(creditCardNumber: String): CreditCardType {
            return when {
                AMEX.regex.matches(creditCardNumber) -> CreditCardType.AMEX
                DINERS.regex.matches(creditCardNumber) -> CreditCardType.DINERS
                VISA.regex.matches(creditCardNumber) -> CreditCardType.VISA
                MAESTRO_13.regex.matches(creditCardNumber) -> CreditCardType.MAESTRO
                MAESTRO_15.regex.matches(creditCardNumber) -> CreditCardType.MAESTRO
                MASTER_CARD.regex.matches(creditCardNumber) -> CreditCardType.MASTERCARD
                DISCOVER.regex.matches(creditCardNumber) -> CreditCardType.DISCOVER
                UNIONPAY_16.regex.matches(creditCardNumber) -> CreditCardType.UNIONPAY
                UNIONPAY_19.regex.matches(creditCardNumber) -> CreditCardType.UNIONPAY
                MAESTRO.regex.matches(creditCardNumber) -> CreditCardType.MAESTRO
                JCB.regex.matches(creditCardNumber) -> CreditCardType.JCB
                else -> CreditCardType.UNKNOWN
            }
        }
    }
}
