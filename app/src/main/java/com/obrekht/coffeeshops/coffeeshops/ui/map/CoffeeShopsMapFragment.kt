package com.obrekht.coffeeshops.coffeeshops.ui.map

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.databinding.FragmentCoffeeShopsMapBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.logo.Padding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoffeeShopsMapFragment : Fragment(R.layout.fragment_coffee_shops_map) {

    private var _binding: FragmentCoffeeShopsMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CoffeeShopsMapViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCoffeeShopsMapBinding.bind(view)

        val map = binding.mapView.mapWindow.map

        ViewCompat.setOnApplyWindowInsetsListener(binding.mapView) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            map.logo.setPadding(Padding(0, insets.bottom))

            WindowInsetsCompat.CONSUMED
        }

        with(binding) {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
