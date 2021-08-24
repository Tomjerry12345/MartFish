package com.martfish.ui.autentikasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.martfish.R

class AutentikasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.autentikasi_activity)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.autentikasi_host_fragment) as NavHostFragment
        navHostFragment.navController
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        supportActionBar!!.show()
    }
}