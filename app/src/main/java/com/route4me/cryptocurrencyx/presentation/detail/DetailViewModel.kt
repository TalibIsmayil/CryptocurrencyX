package com.route4me.cryptocurrencyx.presentation.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.route4me.cryptocurrencyx.common.Resource
import com.route4me.cryptocurrencyx.domain.model.Data
import com.route4me.cryptocurrencyx.domain.usecase.GetCoinDetailUseCase
import com.route4me.cryptocurrencyx.domain.usecase.GetLatestCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCoinDetailUseCase: GetCoinDetailUseCase
) : ViewModel() {

    private val _state: MutableLiveData<Resource<Data>> = MutableLiveData()
    val state
        get() = _state


    fun getLatestCoins(coinID: Int) {
        getCoinDetailUseCase(coinID).onEach { result ->
            state.postValue(result)
        }.launchIn(viewModelScope)
    }
}