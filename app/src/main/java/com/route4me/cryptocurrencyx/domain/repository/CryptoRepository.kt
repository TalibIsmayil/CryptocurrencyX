package com.route4me.cryptocurrencyx.domain.repository

import com.route4me.cryptocurrencyx.domain.model.Data

interface CryptoRepository {

    suspend fun getLatestCoins(): List<Data>?

    suspend fun getSavedCoins(): List<Data>

    suspend fun saveLatestCoins(list: List<Data>)

    suspend fun getCoinByID(id: Int): Data?

}