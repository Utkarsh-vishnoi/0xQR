package io.github.utkarshvishnoi.zeroxqr.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import io.github.utkarshvishnoi.zeroxqr.R
import io.github.utkarshvishnoi.zeroxqr.ZeroXQRApplication
import io.github.utkarshvishnoi.zeroxqr.databinding.ActivityMainBinding

/**
 * Main activity that hosts the navigation architecture for 0xQR.
 *
 * Phase 1 Implementation: Complete UI with navigation between all screens
 * using Navigation Component and Material Design 3 components.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var app: ZeroXQRApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get application instance for security managers
        app = application as ZeroXQRApplication

        // Setup toolbar
        setSupportActionBar(binding.toolbar)

        // Setup navigation
        setupNavigation()
    }

    /**
     * Sets up the Navigation Component with bottom navigation and drawer.
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Configure app bar with navigation
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_encrypt,
                R.id.nav_decrypt,
                R.id.nav_history,
                R.id.nav_settings
            ),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        // Setup bottom navigation
        binding.bottomNavigation.setupWithNavController(navController)

        // Setup navigation drawer
        binding.navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}