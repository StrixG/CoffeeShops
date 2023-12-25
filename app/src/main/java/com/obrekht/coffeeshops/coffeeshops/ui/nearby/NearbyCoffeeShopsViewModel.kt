package com.obrekht.coffeeshops.coffeeshops.ui.nearby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrekht.coffeeshops.auth.data.repository.AuthRepository
import com.obrekht.coffeeshops.coffeeshops.data.repository.CoffeeShopsRepository
import com.obrekht.coffeeshops.coffeeshops.ui.model.CoffeeShop
import com.obrekht.coffeeshops.geolocation.data.repository.GeoLocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class NearbyCoffeeShopsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val geoLocationRepository: GeoLocationRepository,
    private val coffeeShopsRepository: CoffeeShopsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        refreshAll()

        viewModelScope.launch {
            coffeeShopsRepository.getCoffeeShopsStream().collect { coffeeShops ->
                _uiState.update {
                    it.copy(isLoading = false, coffeeShops = coffeeShops)
                }
            }
        }
    }

    fun refreshAll() {
        setLoadingState(true)

        viewModelScope.launch {
            try {
                coffeeShopsRepository.refreshAll()
            } catch (exception: Exception) {
                val errorEvent = when(exception) {
                    is ConnectException -> UiEvent.ErrorConnection
                    else -> UiEvent.ErrorLoading
                }
                _uiEvent.send(errorEvent)
                setLoadingState(false)
            }
        }
    }

    fun refreshCurrentLocation() {
        geoLocationRepository.refreshCurrentLocation()
    }

    private fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }
}

data class UiState(
    val isLoading: Boolean = false,
    val coffeeShops: List<CoffeeShop> = emptyList()
)

sealed interface UiEvent {
    data object ErrorConnection : UiEvent
    data object ErrorLoading : UiEvent
}
