package com.martfish.ui.pembeli.pesanan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.PesananPembeliFragmentBinding
import com.martfish.model.ModelPemesanan
import com.martfish.ui.adapter.PemesananAdapter
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar

class PesananPembeliFragment : Fragment(R.layout.pesanan_pembeli_fragment) {

    private val viewModel: PesananPembeliViewModel by viewModels {
        PesananPembeliViewModel.Factory(FirestoreDatabase())
    }
    private lateinit var binding: PesananPembeliFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = PesananPembeliFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        getPemesanan()
    }

    private fun getPemesanan() {
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
                    val dataPemesanan: List<ModelPemesanan> = data.toObjects()
                    val pemesananAdapter = PemesananAdapter(dataPemesanan, null, null, viewModel)
                    binding.rvListPemesanan.apply {
                        layoutManager = LinearLayoutManager(requireActivity())
                        adapter = pemesananAdapter
                    }
                }
            }
        })
    }

}