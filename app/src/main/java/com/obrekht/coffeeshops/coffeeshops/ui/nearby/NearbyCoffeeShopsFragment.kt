package com.obrekht.coffeeshops.coffeeshops.ui.nearby

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.result.contract.ActivityResultContracts
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
import com.obrekht.coffeeshops.app.utils.hasLocationPermission
import com.obrekht.coffeeshops.app.utils.setOnApplyWindowInsetsListener
import com.obrekht.coffeeshops.core.ui.model.SnackbarAction
import com.obrekht.coffeeshops.databinding.FragmentNearbyCoffeeShopsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NearbyCoffeeShopsFragment : Fragment(R.layout.fragment_nearby_coffee_shops) {

    private var _binding: FragmentNearbyCoffeeShopsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NearbyCoffeeShopsViewModel by viewModels()

    private var adapter: NearbyCoffeeShopsAdapter? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.any { it.value }) {
            viewModel.refreshCurrentLocation()
        } else {
            showErrorSnackbar(R.string.error_location_permission_denied)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentNearbyCoffeeShopsBinding.bind(view)

        binding.buttonOnMap.setOnApplyWindowInsetsListener { button, windowInsets, _, margin ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            button.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = margin.bottom + insets.bottom
            }

            windowInsets
        }

        when {
            requireContext().hasLocationPermission() -> {
                viewModel.refreshCurrentLocation()
            }

            requireActivity().shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                showErrorSnackbar(
                    R.string.error_location_permission_rationale,
                    SnackbarAction(getString(R.string.request)) {
                        requestLocationPermission()
                    }
                )
            }

            else -> requestLocationPermission()
        }

        adapter = NearbyCoffeeShopsAdapter { coffeeShop, _ ->
            navigateToMap(coffeeShop.id)
        }
        with(binding) {
            coffeeShopList.adapter = adapter

            swipeRefresh.setOnRefreshListener {
                viewModel.refreshAll()
            }

            buttonOnMap.setOnClickListener {
                navigateToMap()
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
        adapter = null
    }

    private fun handleUiState(uiState: UiState) {
        binding.swipeRefresh.isRefreshing = uiState.isLoading
        adapter?.submitList(uiState.coffeeShops)
    }

    private fun handleUiEvent(uiEvent: UiEvent) {
        val refreshAction = SnackbarAction(getString(R.string.snackbar_retry)) {
            viewModel.refreshAll()
        }

        when (uiEvent) {
            UiEvent.ErrorConnection -> showErrorSnackbar(
                R.string.error_connection, refreshAction
            )

            UiEvent.ErrorLoading -> showErrorSnackbar(
                R.string.error_loading_coffee_shops, refreshAction
            )
        }
    }

    private fun navigateToMap(coffeeShopId: Long = 0L) {
        val action = NearbyCoffeeShopsFragmentDirections.actionToCoffeeShopsMapFragment(
            coffeeShopId
        )
        findNavController().navigate(action)
    }

    private fun requestLocationPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun showErrorSnackbar(
        @StringRes messageResId: Int,
        snackbarAction: SnackbarAction? = null
    ) {
        Snackbar.make(
            binding.root, messageResId, Snackbar.LENGTH_SHORT
        ).apply {
            anchorView = binding.buttonOnMap
            snackbarAction?.let {
                setAction(it.label, it.listener)
            }
        }.show()
    }
}
