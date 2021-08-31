package com.martfish.ui.fragment.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.DetailProdukFragmentBinding

class DetailProdukFragment : Fragment(R.layout.detail_produk_fragment) {

    private val viewModel: DetailProdukViewModel by viewModels {
        DetailProdukViewModel.Factory(FirestoreDatabase())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DetailProdukFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

}