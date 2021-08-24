package com.martfish.ui.pembeli.pesanan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.databinding.PesananPembeliFragmentBinding

class PesananPembeliFragment : Fragment(R.layout.pesanan_pembeli_fragment) {

    private val viewModel: PesananPembeliViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = PesananPembeliFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

}