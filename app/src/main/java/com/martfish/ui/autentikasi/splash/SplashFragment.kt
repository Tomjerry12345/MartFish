package com.martfish.ui.autentikasi.splash

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.martfish.R

class SplashFragment : Fragment(R.layout.splash_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({
           findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }, 3000)
    }

}