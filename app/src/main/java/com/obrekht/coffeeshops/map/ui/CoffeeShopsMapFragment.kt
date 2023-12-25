package com.obrekht.coffeeshops.map.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.app.utils.setOnApplyWindowInsetsListener
import com.obrekht.coffeeshops.coffeeshops.ui.model.CoffeeShop
import com.obrekht.coffeeshops.databinding.FragmentCoffeeShopsMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.logo.VerticalAlignment
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
            val action = CoffeeShopsMapFragmentDirections.actionToMenuFragment(coffeeShopId)
            findNavController().navigate(action)
            true
        } else {
            false
        }
    }

    private val clusterListener = ClusterListener { cluster ->
        placemarkIconImageProvider?.let {
            cluster.appearance.apply {
                setIcon(
                    ImageProvider.fromResource(requireContext(), R.drawable.ic_coffee_shops_cluster)
                )
                setText("${cluster.placemarks.size}", TextStyle().apply {
                    color = resources.getColor(R.color.dark_brown, context?.theme)
                })
            }
            cluster.addClusterTapListener { cluster ->
                return@addClusterTapListener cluster.placemarks.firstOrNull()?.let {
                    map?.move(
                        CameraPosition(
                            it.geometry,
                            DEFAULT_ZOOM,
                            DEFAULT_AZIMUTH,
                            DEFAULT_TILT
                        ),
                        DEFAULT_CAMERA_ANIMATION,
                        null
                    )
                    true
                } ?: false
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

        binding.mapView.setOnApplyWindowInsetsListener { _, windowInsets, _, _ ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            map?.logo?.apply {
                setPadding(Padding(0, insets.bottom))
                setAlignment(Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM))
            }

            WindowInsetsCompat.CONSUMED
        }

        // Restore map state
        savedInstanceState?.run {
            map?.move(
                CameraPosition(
                    Point(
                        getDouble(KEY_CAMERA_POSITION_LATITUDE),
                        getDouble(KEY_CAMERA_POSITION_LONGITUDE)
                    ),
                    getFloat(KEY_CAMERA_POSITION_ZOOM),
                    getFloat(KEY_CAMERA_POSITION_AZIMUTH),
                    getFloat(KEY_CAMERA_POSITION_TILT)
                )
            )
        } ?: viewModel.restoreCameraPosition()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::handleUiState)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        map?.cameraPosition?.let {
            viewModel.saveCameraPosition(CameraMovePosition(
                it.target.latitude, it.target.longitude,
                it.zoom, it.azimuth, it.tilt
            ))
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        map?.cameraPosition?.run {
            outState.putAll(
                bundleOf(
                    KEY_CAMERA_POSITION_LATITUDE to target.latitude,
                    KEY_CAMERA_POSITION_LONGITUDE to target.longitude,
                    KEY_CAMERA_POSITION_ZOOM to zoom,
                    KEY_CAMERA_POSITION_AZIMUTH to azimuth,
                    KEY_CAMERA_POSITION_TILT to tilt
                )
            )
        }
    }

    private fun handleUiState(uiState: UiState) {
        uiState.moveCameraTo?.let {
            map?.move(
                CameraPosition(
                    Point(it.latitude, it.longitude),
                    it.zoom ?: DEFAULT_ZOOM,
                    it.azimuth ?: DEFAULT_AZIMUTH,
                    it.tilt ?: DEFAULT_TILT
                )
            )
            viewModel.onCameraMoved()
        }

        map?.let {
            placemarkCollection?.clear()
            uiState.coffeeShops.forEach(::addCoffeeShopOnMap)
        }
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
        private const val DEFAULT_AZIMUTH = 0f
        private const val DEFAULT_TILT = 0f

        private val DEFAULT_CAMERA_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)

        private const val KEY_CAMERA_POSITION_LATITUDE = "camera_position_latitude"
        private const val KEY_CAMERA_POSITION_LONGITUDE = "camera_position_longitude"
        private const val KEY_CAMERA_POSITION_ZOOM = "camera_position_zoom"
        private const val KEY_CAMERA_POSITION_AZIMUTH = "camera_position_azimuth"
        private const val KEY_CAMERA_POSITION_TILT = "camera_position_tilt"
    }
}
