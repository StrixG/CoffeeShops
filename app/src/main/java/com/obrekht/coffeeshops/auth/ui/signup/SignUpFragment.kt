package com.obrekht.coffeeshops.auth.ui.signup

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
import com.obrekht.coffeeshops.NavMainDirections
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.app.utils.InsetsAnimationTranslationModifier
import com.obrekht.coffeeshops.app.utils.setKeyboardInsetsAnimationCallback
import com.obrekht.coffeeshops.app.utils.setOnApplyWindowInsetsListener
import com.obrekht.coffeeshops.coffeeshops.ui.navigateToNearbyCoffeeShops
import com.obrekht.coffeeshops.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSignUpBinding.bind(view)

        view.setOnApplyWindowInsetsListener { _, windowInsets, _, _ ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.ime() or WindowInsetsCompat.Type.systemBars()
            )
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }

            windowInsets
        }

        view.setKeyboardInsetsAnimationCallback(InsetsAnimationTranslationModifier.Centered)

        with(binding) {
            buttonSignUp.setOnClickListener {
                signUp()
            }
            buttonLogin.setOnClickListener {
                val action = NavMainDirections.actionGlobalLoginFragment()
                findNavController().navigate(action)
            }

            passwordConfirmEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signUp()
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

            UiEvent.ErrorInvalidEmail -> {
                showSnackbar(R.string.error_invalid_email)
            }

            UiEvent.ErrorEmptyPassword -> {
                showSnackbar(R.string.error_empty_password)
            }

            UiEvent.ErrorPasswordsDoNotMatch -> {
                showSnackbar(R.string.error_passwords_do_not_match)
            }

            UiEvent.ErrorConnection -> {
                showSnackbar(R.string.error_connection)
            }

            UiEvent.ErrorRequest -> {
                showSnackbar(R.string.error_request)
            }

            UiEvent.ErrorAccountIsTaken -> {
                showSnackbar(R.string.error_account_is_taken)
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
            passwordConfirmEditText.isEnabled = active
            buttonSignUp.isEnabled = active
            buttonLogin.isEnabled = active
        }
    }


    private fun signUp() {
        with(binding) {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordConfirmation = passwordConfirmEditText.text.toString()
            viewModel.signUp(email, password, passwordConfirmation)
        }
    }

    private fun showSnackbar(@StringRes resId: Int) {
        Snackbar.make(binding.root, resId, Snackbar.LENGTH_SHORT).show()
    }
}
