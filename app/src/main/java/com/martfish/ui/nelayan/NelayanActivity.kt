package com.martfish.ui.nelayan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.martfish.R

class NelayanActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nelayan_activity)
        val bottomNelayan = findViewById<BottomNavigationView>(R.id.bottomNavNelayan)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeNelayanFragment, R.id.myProdukNelayanFragment, R.id.pesananNelayanFragment, R.id.transaksiNelayanFragment, R.id.profileFragment))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nelayan_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        bottomNelayan.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}