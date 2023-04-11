/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mobilabsolutions.stash.core.CreditCardType

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Entity(
    tableName = "payment_method",
    indices = [
        Index(value = ["paymentMethod_id"], unique = true),
        Index(value = ["user_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("server_user_id"),
            childColumns = arrayOf("user_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PaymentMethod(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo(name = "user_id") val userId: String = "",
    @ColumnInfo(name = "paymentMethod_id") val paymentMethodId: String = "",
    @ColumnInfo(name = "type") val _type: String = "CC",
    @ColumnInfo(name = "cardType") val _cardType: String = "UNKNOWN",
    @ColumnInfo(name = "expiryMonth") val expiryMonth: String = "",
    @ColumnInfo(name = "expiryYear") val expiryYear: String = "",
    @ColumnInfo(name = "mask") val mask: String = "",
    @ColumnInfo(name = "email") val email: String = "",
    @ColumnInfo(name = "iban") val iban: String = ""
) : SampleEntity {

    @delegate:Ignore
    val type by lazy(LazyThreadSafetyMode.NONE) {
        PaymentType.fromStringValue(_type)
    }

    @delegate:Ignore
    val alias by lazy(LazyThreadSafetyMode.NONE) {
        when (type) {
            PaymentType.CC -> "X-$mask$DELIMITER$expiryMonth/${expiryYear.takeLast(2)}"
            PaymentType.SEPA -> iban
            PaymentType.PAY_PAL -> email
            else -> DELIMITER
        }
    }

    @delegate:Ignore
    val cardType by lazy(LazyThreadSafetyMode.NONE) {
        CreditCardType.fromStringValue(_cardType)
    }

    companion object {
        const val DELIMITER = " • "
    }

    fun isSelectedPaymentMethod(selectedMethod: PaymentMethod?): Boolean {
        selectedMethod?.let {
            return it.paymentMethodId == this.paymentMethodId
        }
        return false
    }
}