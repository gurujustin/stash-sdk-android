/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.product

import com.mobilabsolutions.stash.sample.data.DatabaseTransactionRunner
import com.mobilabsolutions.stash.sample.data.daos.ProductDao
import com.mobilabsolutions.stash.sample.data.entities.Product
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
class LocalProductStore @Inject constructor(
    private val transactionRunner: DatabaseTransactionRunner,
    private val productDao: ProductDao
) {
    companion object {
        private val mobilabTShirt = Product(
            image = "image_card_01",
            name = "MobiLab",
            description = "T-Shirt Print",
            price = 2385
        )

        private val notebookPaper = Product(
            image = "image_card_02",
            name = "Notebook Paper",
            description = "Quadrille Pads",
            price = 350
        )

        private val sticker = Product(
            image = "image_card_03",
            name = "MobiLab Sticker",
            description = "12 Sticker Sheets",
            price = 1099
        )

        private val pen = Product(
            image = "image_card_04",
            name = "MobiLab Pen",
            description = "Blue Color",
            price = 1325
        )

        private val femaleTShirt = Product(
            image = "image_card_05",
            name = "MobiLab",
            description = "Female",
            price = 2595
        )

        private val sampleDataList = listOf(mobilabTShirt, notebookPaper, sticker, pen, femaleTShirt)
        private const val ITEM_SIZE = 5
    }

    suspend fun isInitData(): Boolean = productDao.productCount() < ITEM_SIZE

    suspend fun populateInitData() = transactionRunner {
        productDao.insertAll(sampleDataList)
    }

    fun observerProducts() = productDao.entriesObservable()
}