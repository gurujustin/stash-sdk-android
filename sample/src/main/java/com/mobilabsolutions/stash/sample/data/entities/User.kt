/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Entity(
    tableName = "user",
    indices = [
        Index(value = ["server_user_id"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true) @ColumnInfo override val id: Long = 0,
    @ColumnInfo(name = "server_user_id") val userId: String
) : SampleEntity