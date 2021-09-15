package com.route4me.cryptocurrencyx.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.route4me.cryptocurrencyx.domain.model.Data

@Dao
interface CryptoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllCryptos(list: List<Data>)


    @Query("SELECT * FROM cryptos")
    fun getAllCryptos(): List<Data>

}