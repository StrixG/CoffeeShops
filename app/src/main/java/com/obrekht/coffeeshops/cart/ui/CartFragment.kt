package com.obrekht.coffeeshops.cart.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.app.utils.setOnApplyWindowInsetsListener
import com.obrekht.coffeeshops.cart.domain.model.CartMenuItem
import com.obrekht.coffeeshops.databinding.FragmentCartBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels()

    private var adapter: CartItemAdapter? = null

    private val interactionListener = object : CartItemInteractionListener {

        override fun onAdd(cartMenuItem: CartMenuItem, position: Int) {
            viewModel.addToCart(cartMenuItem.id)
        }

        override fun onRemove(cartMenuItem: CartMenuItem, position: Int) {
            viewModel.removeFromCart(cartMenuItem.id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCartBinding.bind(view)

        binding.buttonPay.setOnApplyWindowInsetsListener { button, windowInsets, _, margin ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            button.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = margin.bottom + insets.bottom
            }

            windowInsets
        }

        adapter = CartItemAdapter(interactionListener)
        binding.itemList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::handleUiState)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        adapter = null
    }

    private fun handleUiState(uiState: UiState) {
        with(binding) {
            if (uiState.isLoading) {
                progressBar.show()
                itemList.isVisible = false
            } else {
                progressBar.hide()
                itemList.isVisible = true
            }

            emptyText.isVisible = !uiState.isLoading && uiState.isEmpty

            buttonPay.isEnabled = uiState.canProceed
            buttonPay.text = getString(R.string.pay, uiState.totalPrice)

            adapter?.submitList(uiState.menuItems)
        }
    }
}
