package com.obrekht.coffeeshops.cart.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrekht.coffeeshops.cart.data.repository.CartRepository
import com.obrekht.coffeeshops.cart.domain.GetCartMenuItemsStreamUseCase
import com.obrekht.coffeeshops.cart.domain.model.CartMenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val getCartMenuItemsStream: GetCartMenuItemsStreamUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            setLoadingState(true)

            getCartMenuItemsStream().collectLatest { cartMenuItems ->
                val canProceed = cartMenuItems.any { it.count > 0 }
                val totalPrice = cartMenuItems.fold(0) { acc, cartMenuItem ->
                    acc + cartMenuItem.price * cartMenuItem.count
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        canProceed = canProceed,
                        isEmpty = cartMenuItems.isEmpty(),
                        menuItems = cartMenuItems,
                        totalPrice = totalPrice
                    )
                }
            }

            setLoadingState(false)
        }
    }

    fun addToCart(menuItemId: Long) {
        viewModelScope.launch {
            cartRepository.add(menuItemId)
        }
    }

    fun removeFromCart(menuItemId: Long) {
        viewModelScope.launch {
            cartRepository.remove(menuItemId)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }
}

data class UiState(
    val isLoading: Boolean = false,
    val canProceed: Boolean = false,
    val isEmpty: Boolean = false,
    val menuItems: List<CartMenuItem> = emptyList(),
    val totalPrice: Int = 0
)
