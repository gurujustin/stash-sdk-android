/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.util

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mobilabsolutions.stash.core.CreditCardType
import com.mobilabsolutions.stash.sample.R
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.data.entities.PaymentType
import com.mobilabsolutions.stash.sample.data.entities.Product
import com.mobilabsolutions.stash.sample.data.entities.ProductType.MOBILAB_PEN
import com.mobilabsolutions.stash.sample.data.entities.ProductType.MOBILAB_STICKER
import com.mobilabsolutions.stash.sample.data.entities.ProductType.MOBILAB_T_SHIRT
import com.mobilabsolutions.stash.sample.data.entities.ProductType.NOTEBOOK_PAPER
import com.mobilabsolutions.stash.sample.extensions.priceWithCurrencyString

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
@BindingAdapter("priceWithCurrency")
fun priceWithCurrency(textView: TextView, price: Int) {
    textView.text = priceWithCurrencyString(price)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("priceWithCurrencyAndLabel")
fun priceWithCurrencyAndLabel(textView: TextView, price: Int) {
    textView.text = "PAY ${priceWithCurrencyString(price)}"
}

@BindingAdapter("imageByProductType")
fun imageByProductType(imageView: ImageView, product: Product) {
    val resId = when (product.productType) {
        MOBILAB_T_SHIRT -> R.drawable.image_card_01
        NOTEBOOK_PAPER -> R.drawable.image_card_02
        MOBILAB_STICKER -> R.drawable.image_card_03
        MOBILAB_PEN -> R.drawable.image_card_04
        else -> R.drawable.image_card_05
    }
    imageView.setImageResource(resId)
}

@BindingAdapter("paymentImageByType")
fun paymentImageByType(imageView: ImageView, paymentMethod: PaymentMethod) {
    val resId = when (paymentMethod.type) {
        PaymentType.CC -> when (paymentMethod.cardType) {
            CreditCardType.JCB -> R.drawable.ic_pm_card_jcb
            CreditCardType.AMEX -> R.drawable.ic_pm_card_amex
            CreditCardType.DINERS -> R.drawable.ic_pm_card_diners
            CreditCardType.VISA -> R.drawable.ic_pm_card_visa
            CreditCardType.MASTERCARD -> R.drawable.ic_pm_card_mastercard
            CreditCardType.DISCOVER -> R.drawable.ic_pm_card_discover
            CreditCardType.UNIONPAY -> R.drawable.ic_pm_card_unionpay
            CreditCardType.MAESTRO -> R.drawable.ic_pm_card_maestro
            CreditCardType.UNKNOWN -> R.drawable.ic_pm_card_unknown
            else -> R.drawable.ic_pm_card_unknown
        }
        PaymentType.SEPA -> R.drawable.ic_pm_sepa
        PaymentType.PAY_PAL -> R.drawable.ic_pm_paypal
        else -> R.drawable.ic_pm_card_unknown
    }
    imageView.setImageResource(resId)
}