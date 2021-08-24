package com.martfish.ui.nelayan.pesanan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.databinding.PesananNelayanFragmentBinding

class PesananNelayanFragment : Fragment(R.layout.pesanan_nelayan_fragment) {

    private val viewModel: PesananNelayanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = PesananNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

}