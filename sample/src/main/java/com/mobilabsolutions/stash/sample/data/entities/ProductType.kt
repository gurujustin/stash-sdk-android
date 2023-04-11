/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.entities

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 15-04-2019.
 */
enum class ProductType(val productName: String) {
    MOBILAB_T_SHIRT("MobiLab"),
    NOTEBOOK_PAPER("Notebook Paper"),
    MOBILAB_STICKER("MobiLab Sticker"),
    MOBILAB_PEN("MobiLab Pen");

    companion object {
        fun fromProductName(value: String): ProductType? = values().firstOrNull { it.productName == value }
    }
}