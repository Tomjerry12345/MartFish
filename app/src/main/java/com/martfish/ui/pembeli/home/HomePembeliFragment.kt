package com.martfish.ui.pembeli.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.HomePembeliFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.ui.adapter.ProdukAdapter
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar

class HomePembeliFragment : Fragment(R.layout.home_pembeli_fragment) {


    private val viewModel: HomePembeliViewModel by viewModels {
        HomePembeliViewModel.Factory(FirestoreDatabase())
    }
    private lateinit var binding: HomePembeliFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomePembeliFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        getAllDataProduk()
    }

    private fun getAllDataProduk() {
        viewModel.data.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {}
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(requireView(), result.error, "error")
                }
                is Response.Changed -> {
                    val data = result.data as QuerySnapshot
                    showLogAssert("data", "$data")
                    val dataProduk: List<ModelProduk> = data.toObjects()
                    val produkAdapter = ProdukAdapter(dataProduk)
                    binding.rvListProduk.apply {
                        layoutManager = GridLayoutManager(requireActivity(), 2)
                        adapter = produkAdapter
                    }
                }
            }
        })
    }

}