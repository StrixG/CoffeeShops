package com.obrekht.coffeeshops.map.ui.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrekht.coffeeshops.coffeeshops.data.model.CoffeeShopPoint
import com.obrekht.coffeeshops.coffeeshops.data.repository.CoffeeShopsRepository
import com.obrekht.coffeeshops.coffeeshops.ui.model.CoffeeShop
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoffeeShopsMapViewModel @Inject constructor(
    private val coffeeShopsRepository: CoffeeShopsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = CoffeeShopsMapFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (args.coffeeShopId != 0L) {
            viewModelScope.launch {
                coffeeShopsRepository.getCoffeeShopById(args.coffeeShopId)?.let { coffeeShop ->
                    _uiState.update {
                        it.copy(moveCameraTo = coffeeShop.point)
                    }
                }
            }
        }

        viewModelScope.launch {
            coffeeShopsRepository.getCoffeeShopsStream().collect { coffeeShops ->
                _uiState.update {
                    it.copy(coffeeShops = coffeeShops)
                }
            }
        }
    }

    fun onCameraMoved() {
        _uiState.update {
            it.copy(moveCameraTo = null)
        }
    }
}

data class UiState(
    val coffeeShops: List<CoffeeShop> = emptyList(),
    val moveCameraTo: CoffeeShopPoint? = null
)
