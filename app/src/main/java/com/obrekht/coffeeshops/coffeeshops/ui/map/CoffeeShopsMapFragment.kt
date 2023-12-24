package com.obrekht.coffeeshops.coffeeshops.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.coffeeshops.ui.model.CoffeeShop
import com.obrekht.coffeeshops.databinding.FragmentCoffeeShopsMapBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CoffeeShopsMapFragment : Fragment(R.layout.fragment_coffee_shops_map) {

    private var _binding: FragmentCoffeeShopsMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CoffeeShopsMapViewModel by viewModels()

    private var map: Map? = null
    private var placemarkCollection: ClusterizedPlacemarkCollection? = null
    private var placemarkIconImageProvider: ImageProvider? = null

    private val placemarkTapListener = MapObjectTapListener { mapObject, _ ->
        if (mapObject is PlacemarkMapObject && mapObject.userData is Long) {
            val coffeeShopId = mapObject.userData as Long
            // TODO: Navigate to menu fragment
            true
        } else {
            false
        }
    }

    private val clusterListener = ClusterListener { cluster ->
        placemarkIconImageProvider?.let {
            cluster.appearance.apply {
                setIcon(
                    ImageProvider.fromResource(
                        requireContext(), R.drawable.ic_coffee_shops_cluster
                    )
                )
                setText("${cluster.placemarks.size}", TextStyle().apply {
                    color = resources.getColor(R.color.dark_brown, context?.theme)
                })
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCoffeeShopsMapBinding.bind(view)

        map = binding.mapView.mapWindow.map.apply {
            placemarkCollection = mapObjects.addClusterizedPlacemarkCollection(clusterListener)
        }
        placemarkIconImageProvider = ImageProvider.fromResource(
            requireContext(), R.drawable.ic_coffee_shop_placemark
        )

        ViewCompat.setOnApplyWindowInsetsListener(binding.mapView) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            map?.logo?.setPadding(Padding(0, insets.bottom))

            WindowInsetsCompat.CONSUMED
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    uiState.moveCameraTo?.let {
                        map?.move(
                            CameraPosition(
                                Point(it.latitude, it.longitude),
                                DEFAULT_ZOOM,
                                DEFAULT_ROTATION,
                                DEFAULT_TILT
                            )
                        )
                        viewModel.onCameraMoved()
                    }

                    map?.let {
                        placemarkCollection?.clear()
                        uiState.coffeeShops.forEach(::addCoffeeShopOnMap)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        map = null
        placemarkCollection = null
        placemarkIconImageProvider = null
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

    private fun addCoffeeShopOnMap(coffeeShop: CoffeeShop) {
        val placemarkCollection = placemarkCollection ?: return
        val textColor = resources.getColor(R.color.brown, context?.theme)

        placemarkCollection.addPlacemark().apply {
            geometry = Point(coffeeShop.point.latitude, coffeeShop.point.longitude)
            userData = coffeeShop.id
            placemarkIconImageProvider?.let {
                setIcon(it)
            }
            setText(coffeeShop.name, TextStyle().apply {
                placement = TextStyle.Placement.BOTTOM
                color = textColor
                outlineColor = Color.TRANSPARENT
            })
            addTapListener(placemarkTapListener)
        }
        placemarkCollection.clusterPlacemarks(60.0, 15)
    }

    companion object {
        private const val DEFAULT_ZOOM = 15f
        private const val DEFAULT_ROTATION = 0f
        private const val DEFAULT_TILT = 0f
    }
}
