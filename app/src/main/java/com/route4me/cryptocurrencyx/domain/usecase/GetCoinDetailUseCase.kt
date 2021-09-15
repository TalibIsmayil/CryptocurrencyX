package com.route4me.cryptocurrencyx.domain.usecase

import com.route4me.cryptocurrencyx.common.Resource
import com.route4me.cryptocurrencyx.domain.model.Data
import com.route4me.cryptocurrencyx.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetCoinDetailUseCase @Inject constructor(
    private val repository: CryptoRepository
) {

    operator fun invoke(coinId: Int): Flow<Resource<Data>> = flow {
        try {
            emit(Resource.Loading())
            val coins = repository.getCoinByID(coinId)
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