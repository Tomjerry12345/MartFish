package com.martfish.ui.nelayan.myProduk.editProduk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.databinding.EditProdukNelayanFragmentBinding

class EditProdukNelayanFragment : Fragment(R.layout.edit_produk_nelayan_fragment) {

    private val viewModel: EditProdukNelayanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = EditProdukNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

}