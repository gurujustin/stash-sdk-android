package com.mobilabsolutions.stash.core.internal.api.backend.model

import com.google.gson.annotations.SerializedName
import com.mobilabsolutions.stash.core.internal.api.backend.v1.AliasExtra

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-08-2019.
 */
data class CreateAliasResponseDto(
    val aliasId: String,
    val extra: AliasExtra,
    @SerializedName("psp")
    val pspExtra: Map<String, String>
)