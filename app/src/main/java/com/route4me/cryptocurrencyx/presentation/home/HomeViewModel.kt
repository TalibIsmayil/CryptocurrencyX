package com.route4me.cryptocurrencyx.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.route4me.cryptocurrencyx.common.Resource
import com.route4me.cryptocurrencyx.domain.model.Data
import com.route4me.cryptocurrencyx.domain.usecase.GetLatestCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLatestCoinsUseCase: GetLatestCoinsUseCase
) : ViewModel() {

    private val _state: MutableLiveData<Resource<List<Data>>> = MutableLiveData()
    val state
        get() = _state

    init {
        getLatestCoins()
    }

    private fun getLatestCoins() {
        getLatestCoinsUseCase().onEach { result ->
            state.postValue(result)
        }.launchIn(viewModelScope)
    }
}