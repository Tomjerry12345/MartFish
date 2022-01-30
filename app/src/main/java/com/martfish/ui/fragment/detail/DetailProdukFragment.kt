package com.martfish.ui.fragment.detail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.DetailProdukFragmentBinding
import com.martfish.model.ModelKomentar
import com.martfish.model.ModelProduk
import com.martfish.ui.adapter.KomentarAdapter
import com.martfish.utils.*

class DetailProdukFragment : Fragment(R.layout.detail_produk_fragment) {

    private val viewModel: DetailProdukViewModel by viewModels {
        DetailProdukViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: DetailProdukFragmentBinding

    val dataUsers = SavedData.getDataUsers()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DetailProdukFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val argument = SavedData.getDataProduk()

        viewModel.modelProduk = argument

        if (argument != null) {
            setInitValue(argument)
        }

        getKomentar()

        binding.mbBeli.setOnClickListener {
            if (dataUsers?.jenisAkun == "Nelayan")
                view.findNavController()
                    .navigate(R.id.action_detailProdukFragment_to_dataPembeliFragment)
            else
                view.findNavController()
                    .navigate(R.id.action_detailProdukFragment2_to_dataPembeliFragment2)
        }

        binding.mbKomentar.setOnClickListener {
            if (dataUsers?.jenisAkun == "Nelayan")
                view.findNavController()
                    .navigate(R.id.action_detailProdukFragment_to_komentarFragment)
            else
                view.findNavController()
                    .navigate(R.id.action_detailProdukFragment2_to_komentarFragment2)

        }

    }

    private fun setInitValue(argument: ModelProduk) {
        viewModel.namaProduk.value = argument.kategori
        viewModel.hargaPerEkor.value = argument.hargaPerEkor
        viewModel.hargaPerGompo.value = argument.hargaPerGompo
        viewModel.hargaPerKg.value = argument.hargaPerKg
        viewModel.kecamatan.value = argument.kecamatan
        viewModel.kelurahan.value = argument.kelurahan
        viewModel.alamat.value = argument.alamat
        viewModel.rating.value = "(${argument.rating})"

        binding.ratingProduk.rating = argument.rating!!

        Glide
            .with(requireActivity())
            .load(argument.image)
            .centerCrop()
            .into(binding.imageProduk)

    }

    private fun getKomentar() {
        viewModel.getKomentar.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Response.Success -> {
                }
                is Response.Error -> {
                    showLogAssert("error detail produk", result.error)
                    showSnackbar(requireView(), result.error, "error")
                }
                is Response.Changed -> {
                    val data = result.data as QuerySnapshot
                    showLogAssert("data detail produk", "$data")
                    val dataKomentar: List<ModelKomentar> = data.toObjects()
                    val komentarAdapter = KomentarAdapter(
                        dataKomentar
                    )
                    binding.rvKomentar.apply {
                        layoutManager = LinearLayoutManager(requireActivity())
                        adapter = komentarAdapter
                    }
                }
            }
        }
    }

}