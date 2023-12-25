package com.obrekht.coffeeshops.menu.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrekht.coffeeshops.cart.data.repository.CartRepository
import com.obrekht.coffeeshops.cart.domain.GetCartMenuItemsStreamUseCase
import com.obrekht.coffeeshops.cart.domain.model.CartMenuItem
import com.obrekht.coffeeshops.menu.data.repository.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val cartRepository: CartRepository,
    private val getCartMenuItemsStreamUseCase: GetCartMenuItemsStreamUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val args = MenuFragmentArgs.fromSavedStateHandle(savedStateHandle)

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        refresh()

        viewModelScope.launch {
            cartRepository.deleteAll()

            getCartMenuItemsStreamUseCase(args.coffeeShopId).collectLatest { cartMenuItems ->
                val canProceed = cartMenuItems.any { it.count > 0 }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        canProceed = canProceed,
                        menuItems = cartMenuItems
                    )
                }
            }
        }
    }

    fun refresh() {
        setLoadingState(true)

        viewModelScope.launch {
            try {
                menuRepository.refreshById(args.coffeeShopId)
            } catch (exception: Exception) {
                val errorEvent = when (exception) {
                    is ConnectException -> UiEvent.ErrorConnection
                    else -> UiEvent.ErrorLoading
                }
                _uiEvent.send(errorEvent)
                setLoadingState(false)
            }
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
    val menuItems: List<CartMenuItem> = emptyList()
)

sealed interface UiEvent {
    data object ErrorConnection : UiEvent
    data object ErrorLoading : UiEvent
}
