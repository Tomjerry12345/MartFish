package com.martfish.ui.nelayan.myProduk.tambahProduk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.databinding.TambahProdukNelayanFragmentBinding

class TambahProdukNelayanFragment : Fragment(R.layout.tambah_produk_nelayan_fragment) {

    private val viewModel: TambahProdukNelayanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = TambahProdukNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

}