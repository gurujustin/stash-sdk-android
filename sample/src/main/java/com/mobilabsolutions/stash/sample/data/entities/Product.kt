/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Entity(
    tableName = "product"
)
data class Product(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") override val id: Long = 0,
    @ColumnInfo val name: String? = null,
    @ColumnInfo val image: String? = null,
    @ColumnInfo val description: String? = null,
    @ColumnInfo val price: Int = 0
) : SampleEntity {
    @delegate:Ignore
    val productType by lazy(LazyThreadSafetyMode.NONE) {
        ProductType.fromProductName(name ?: "")
    }
}