package com.route4me.cryptocurrencyx.domain.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class USD(
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("percent_change_1h")
    val percentChange1h: Double? = null,
    @SerializedName("percent_change_24h")
    val percentChange24h: Double? = null,
    @SerializedName("percent_change_7d")
    val percentChange7d: Double? = null,
)