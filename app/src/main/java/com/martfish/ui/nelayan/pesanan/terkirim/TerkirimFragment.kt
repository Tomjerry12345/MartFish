package com.martfish.ui.nelayan.pesanan.terkirim

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.TerkirimFragmentBinding
import com.martfish.model.ModelPemesanan
import com.martfish.ui.adapter.PemesananAdapter
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar

class TerkirimFragment : Fragment(R.layout.terkirim_fragment) {

    private val viewModel: TerkirimViewModel by viewModels {
        TerkirimViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: TerkirimFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TerkirimFragmentBinding.bind(view)

        getPemesanan()
    }

    private fun getPemesanan() {
        viewModel.data.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Success -> {
                }
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(requireView(), result.error, "error")
                }
                is Response.Changed -> {
                    val data = result.data as QuerySnapshot
                    showLogAssert("data", "$data")
                    val dataPemesanan: List<ModelPemesanan> = data.toObjects()
                    val pemesananAdapter = PemesananAdapter(
                        dataPemesanan,
                        null,
                        viewModel,
                        null
                    )
                    binding.rvListPemesanan.apply {
                        layoutManager = LinearLayoutManager(requireActivity())
                        adapter = pemesananAdapter
                    }
                }
            }
        }
    }

}