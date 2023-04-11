/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.resultentities

import androidx.room.Embedded
import androidx.room.Relation
import com.mobilabsolutions.stash.sample.data.entities.Cart
import com.mobilabsolutions.stash.sample.data.entities.Product
import java.util.Objects

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
class CartWithProduct {
    @Embedded
    var entry: Cart? = null

    @Relation(parentColumn = "product_id", entityColumn = "id")
    var relations: List<Product> = emptyList()

    val product: Product
        get() {
            assert(relations.size == 1)
            return relations[0]
        }

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is CartWithProduct -> entry == other.entry && relations == other.relations
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(entry, relations)
}