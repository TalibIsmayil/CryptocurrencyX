package com.route4me.cryptocurrencyx.domain.model

import androidx.annotation.Keep
import androidx.room.Embedded
import com.google.gson.annotations.SerializedName

@Keep
data class Quote(
    @SerializedName("USD")
    @Embedded
    val usd: USD? = null
)