package com.obrekht.coffeeshops.auth.ui.login

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.app.utils.InsetsAnimationTranslationModifier
import com.obrekht.coffeeshops.app.utils.setKeyboardInsetsAnimationCallback
import com.obrekht.coffeeshops.app.utils.setOnApplyWindowInsetsListener
import com.obrekht.coffeeshops.auth.ui.navigateToSignUp
import com.obrekht.coffeeshops.coffeeshops.ui.navigateToNearbyCoffeeShops
import com.obrekht.coffeeshops.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentLoginBinding.bind(view)

        view.setOnApplyWindowInsetsListener { _, windowInsets, _, _ ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.ime() or WindowInsetsCompat.Type.systemBars()
            )
            view.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = insets.bottom
            }

            windowInsets
        }

        view.setKeyboardInsetsAnimationCallback(InsetsAnimationTranslationModifier.Centered)

        with(binding) {
            buttonLogin.setOnClickListener {
                logIn()
            }
            buttonSignUp.setOnClickListener {
                findNavController().navigateToSignUp()
            }

            passwordEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    logIn()
                    true
                } else {
                    false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect(::handleUiState)
                }
                launch {
                    viewModel.uiEvent.collect(::handleUiEvent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleUiState(uiState: UiState) {
        if (uiState.isLoading) {
            binding.progressBar.show()
        } else {
            binding.progressBar.hide()
        }
        setInteractionsActive(!uiState.isLoading)
    }

    private fun handleUiEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            UiEvent.ErrorUnknown -> {
                showSnackbar(R.string.error_unknown)
            }

            UiEvent.ErrorEmptyEmail -> {
                showSnackbar(R.string.error_empty_email)
            }

            UiEvent.ErrorEmptyPassword -> {
                showSnackbar(R.string.error_empty_password)
            }

            UiEvent.ErrorConnection -> {
                showSnackbar(R.string.error_connection)
            }

            UiEvent.ErrorRequest -> {
                showSnackbar(R.string.error_request)
            }

            UiEvent.ErrorInvalidCredentials -> {
                showSnackbar(R.string.error_invalid_credentials)
            }

            UiEvent.NavigateToNearbyCoffeeShops -> {
                findNavController().navigateToNearbyCoffeeShops(true)
            }
        }
    }

    private fun setInteractionsActive(active: Boolean) {
        with(binding) {
            emailEditText.isEnabled = active
            passwordEditText.isEnabled = active
            buttonSignUp.isEnabled = active
            buttonLogin.isEnabled = active
        }
    }

    private fun logIn() {
        with(binding) {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            viewModel.logIn(email, password)
        }
    }

    private fun showSnackbar(@StringRes resId: Int) {
        Snackbar.make(binding.root, resId, Snackbar.LENGTH_SHORT).show()
    }
}
