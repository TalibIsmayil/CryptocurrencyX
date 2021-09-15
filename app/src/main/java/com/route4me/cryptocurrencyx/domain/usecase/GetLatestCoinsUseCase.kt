package com.route4me.cryptocurrencyx.domain.usecase

import android.content.Context
import com.route4me.cryptocurrencyx.common.Constants
import com.route4me.cryptocurrencyx.common.PreferenceHelper
import com.route4me.cryptocurrencyx.common.PreferenceHelper.set
import com.route4me.cryptocurrencyx.common.Resource
import com.route4me.cryptocurrencyx.domain.model.Data
import com.route4me.cryptocurrencyx.domain.repository.CryptoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetLatestCoinsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: CryptoRepository
) {

    operator fun invoke(): Flow<Resource<List<Data>>> = flow {
        try {
            emit(Resource.Loading())
            val lastRequestedTimestamp =
                PreferenceHelper.defaultPrefs(context).getLong(Constants.TIMESTAMP_KEY, -1L)//13:00
            var coins: List<Data>? = null
            val tenAgo: Long = System.currentTimeMillis() - Constants.TEN_MINUTES//12:58
            if (lastRequestedTimestamp < tenAgo || lastRequestedTimestamp == -1L) {
                coins = repository.getLatestCoins()
                PreferenceHelper.defaultPrefs(context)[Constants.TIMESTAMP_KEY] =
                    System.currentTimeMillis()
                coins?.let { repository.saveLatestCoins(it) }
            } else {
                coins = repository.getSavedCoins()
            }
            coins?.let {
                emit(Resource.Success(it))
            } ?: emit(Resource.Error("An unexpected error occured."))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occured."))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

}