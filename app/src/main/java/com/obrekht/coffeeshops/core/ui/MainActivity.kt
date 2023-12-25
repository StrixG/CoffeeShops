package com.obrekht.coffeeshops.core.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.obrekht.coffeeshops.R
import com.obrekht.coffeeshops.databinding.ActivityMainBinding
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val statusBarStyle = SystemBarStyle.light(
            Color.TRANSPARENT, Color.TRANSPARENT
        )
        val navigationBarStyle = SystemBarStyle.light(
            Color.TRANSPARENT, getColor(R.color.brown)
        )
        enableEdgeToEdge(statusBarStyle, navigationBarStyle)
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.sign_up_fragment, R.id.nearby_coffee_shops_fragment)
        )
        val navController = binding.fragmentContainer.getFragment<NavHostFragment>().navController
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
