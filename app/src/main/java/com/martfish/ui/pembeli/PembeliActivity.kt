package com.martfish.ui.pembeli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.martfish.R

class PembeliActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pembeli_activity)
        val bottomPembeli = findViewById<BottomNavigationView>(R.id.bottomNavPembeli)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homePembeliFragment,  R.id.pesananPembeliFragment))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.pembeli_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        bottomPembeli.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}