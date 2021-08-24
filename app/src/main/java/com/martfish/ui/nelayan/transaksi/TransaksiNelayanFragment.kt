package com.martfish.ui.nelayan.transaksi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.databinding.TransaksiNelayanFragmentBinding

class TransaksiNelayanFragment : Fragment(R.layout.transaksi_nelayan_fragment) {

    private val viewModel: TransaksiNelayanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = TransaksiNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

}