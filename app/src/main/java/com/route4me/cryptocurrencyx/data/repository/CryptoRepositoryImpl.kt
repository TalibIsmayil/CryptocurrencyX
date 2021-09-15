package com.route4me.cryptocurrencyx.data.repository

import com.route4me.cryptocurrencyx.data.local.CryptoDao
import com.route4me.cryptocurrencyx.data.remote.CryptoAPI
import com.route4me.cryptocurrencyx.domain.model.Data
import com.route4me.cryptocurrencyx.domain.repository.CryptoRepository
import javax.inject.Inject

class CryptoRepositoryImpl @Inject constructor(
    private val api: CryptoAPI,
    private val dao: CryptoDao
) : CryptoRepository {


    override suspend fun getLatestCoins(): List<Data>? {
        return api.getLatest().data
    }

    override suspend fun getSavedCoins(): List<Data> {
        return dao.getAllCryptos()
    }

    override suspend fun saveLatestCoins(list: List<Data>) {
        dao.saveAllCryptos(list)
    }

    override suspend fun getCoinByID(id: Int): Data? {
        return api.getLatest().data?.find { it.id == id }
    }
}