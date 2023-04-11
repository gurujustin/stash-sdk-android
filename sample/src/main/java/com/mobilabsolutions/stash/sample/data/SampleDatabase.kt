/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mobilabsolutions.stash.sample.data.daos.CartDao
import com.mobilabsolutions.stash.sample.data.daos.PaymentMethodDao
import com.mobilabsolutions.stash.sample.data.daos.ProductDao
import com.mobilabsolutions.stash.sample.data.daos.UserDao
import com.mobilabsolutions.stash.sample.data.entities.Cart
import com.mobilabsolutions.stash.sample.data.entities.PaymentMethod
import com.mobilabsolutions.stash.sample.data.entities.Product
import com.mobilabsolutions.stash.sample.data.entities.User

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Database(
    entities = [
        Product::class,
        Cart::class,
        PaymentMethod::class,
        User::class
    ],
    version = 7
)
abstract class SampleDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun userDao(): UserDao
}
