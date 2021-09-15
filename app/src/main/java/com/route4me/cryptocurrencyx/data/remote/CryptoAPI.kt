package com.route4me.cryptocurrencyx.data.remote

import com.route4me.cryptocurrencyx.BuildConfig
import com.route4me.cryptocurrencyx.domain.model.LatestCoinsRoot
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoAPI {

    companion object {
        private fun getVersion(): String {
            return "v1"
        }
    }

    @GET("{version}/cryptocurrency/listings/latest")
    suspend fun getLatest(
        @Path("version") version: String = getVersion(),
        @Query("CMC_PRO_API_KEY") apiKey: String = BuildConfig.API_KEY,
    ): LatestCoinsRoot


}