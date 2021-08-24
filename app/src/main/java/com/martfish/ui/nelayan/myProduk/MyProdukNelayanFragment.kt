package com.martfish.ui.nelayan.myProduk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.databinding.MyProdukNelayanFragmentBinding

class MyProdukNelayanFragment : Fragment(R.layout.my_produk_nelayan_fragment) {

    private val viewModel: MyProdukNelayanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = MyProdukNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

}