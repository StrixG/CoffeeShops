package com.obrekht.coffeeshops.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrekht.coffeeshops.auth.data.model.AuthState
import com.obrekht.coffeeshops.auth.data.model.exception.InvalidCredentialsException
import com.obrekht.coffeeshops.auth.data.repository.AuthRepository
import com.obrekht.coffeeshops.core.data.model.EmptyBodyException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.state.first { it is AuthState.Authorized }

            _uiEvent.send(UiEvent.NavigateToNearbyCoffeeShops)
        }
    }

    fun logIn(email: String, password: String) {
        if (uiState.value.isLoading) return

        val validationErrorEvent = if (email.isBlank()) {
            UiEvent.ErrorEmptyEmail
        } else if (password.isBlank()) {
            UiEvent.ErrorEmptyPassword
        } else null

        if (validationErrorEvent != null) {
            viewModelScope.launch {
                _uiEvent.send(validationErrorEvent)
            }
            return
        }

        viewModelScope.launch {
            try {
                setLoadingState(true)
                authRepository.logIn(email, password)
            } catch (exception: Exception) {
                val errorEvent = when (exception) {
                    is ConnectException -> UiEvent.ErrorConnection
                    is EmptyBodyException, is HttpException -> UiEvent.ErrorRequest
                    is InvalidCredentialsException -> UiEvent.ErrorInvalidCredentials
                    else -> {
                        exception.printStackTrace()
                        UiEvent.ErrorUnknown
                    }
                }

                _uiEvent.send(errorEvent)
                setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(isLoading = isLoading)
        }
    }
}

data class UiState(
    val isLoading: Boolean = false
)

sealed interface UiEvent {
    data object ErrorUnknown : UiEvent
    data object ErrorEmptyEmail : UiEvent
    data object ErrorEmptyPassword : UiEvent
    data object ErrorConnection : UiEvent
    data object ErrorRequest : UiEvent
    data object ErrorInvalidCredentials : UiEvent

    data object NavigateToNearbyCoffeeShops : UiEvent
}
