package com.route4me.cryptocurrencyx.domain.model

import androidx.annotation.Keep

@Keep
data class LatestCoinsRoot(
    val `data`: List<Data>? = null,
)