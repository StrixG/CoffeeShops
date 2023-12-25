package com.obrekht.coffeeshops.menu.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
import com.obrekht.coffeeshops.app.utils.setOnApplyWindowInsetsListener
import com.obrekht.coffeeshops.cart.domain.model.CartMenuItem
import com.obrekht.coffeeshops.core.ui.model.SnackbarAction
import com.obrekht.coffeeshops.databinding.FragmentMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.fragment_menu) {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MenuViewModel by viewModels()

    private var adapter: CartMenuItemAdapter? = null

    private val interactionListener = object : MenuItemInteractionListener {
        override fun onAdd(cartMenuItem: CartMenuItem, position: Int) {
            viewModel.addToCart(cartMenuItem.id)
        }

        override fun onRemove(cartMenuItem: CartMenuItem, position: Int) {
            viewModel.removeFromCart(cartMenuItem.id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentMenuBinding.bind(view)

        binding.buttonProceedToPayment.setOnApplyWindowInsetsListener { button, windowInsets, _, margin ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            button.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = margin.bottom + insets.bottom
            }

            windowInsets
        }

        adapter = CartMenuItemAdapter(interactionListener)
        with(binding) {
            val spacingSize = resources.getDimensionPixelOffset(R.dimen.small_spacing)
            menuList.addItemDecoration(SpacingItemDecoration(spacingSize))
            menuList.adapter = adapter

            swipeRefresh.setOnRefreshListener {
                viewModel.refresh()
            }

            buttonProceedToPayment.setOnClickListener {
                val action = MenuFragmentDirections.actionToCartFragment()
                findNavController().navigate(action)
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

    private fun handleUiState(uiState: UiState) {
        with(binding) {
            swipeRefresh.isRefreshing = uiState.isLoading
            buttonProceedToPayment.isEnabled = uiState.canProceed
        }
        adapter?.submitList(uiState.menuItems)
    }

    private fun handleUiEvent(uiEvent: UiEvent) {
        val action = SnackbarAction(getString(R.string.snackbar_retry)) {
            viewModel.refresh()
        }

        when (uiEvent) {
            UiEvent.ErrorConnection -> showErrorSnackbar(
                R.string.error_connection, action
            )

            UiEvent.ErrorLoading -> showErrorSnackbar(
                R.string.error_loading_menu, action
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }

    private fun showErrorSnackbar(
        @StringRes messageResId: Int,
        snackbarAction: SnackbarAction? = null
    ) {
        Snackbar.make(
            binding.root, messageResId, Snackbar.LENGTH_SHORT
        ).apply {
            anchorView = binding.buttonProceedToPayment
            snackbarAction?.let {
                setAction(it.label, it.listener)
            }
        }.show()
    }
}
