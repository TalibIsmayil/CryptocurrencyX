package com.route4me.cryptocurrencyx.domain.model

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Keep
@Entity(tableName = "cryptos")
data class Data(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String? = null,
    @Embedded
    val quote: Quote? = null,
    val symbol: String? = null,
    @SerializedName("cmc_rank")
    val cmcRank: Int? = null
)