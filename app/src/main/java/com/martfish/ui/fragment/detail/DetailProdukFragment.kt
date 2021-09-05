package com.martfish.ui.fragment.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.DetailProdukFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.utils.Constant

class DetailProdukFragment : Fragment(R.layout.detail_produk_fragment) {

    private val viewModel: DetailProdukViewModel by viewModels {
        DetailProdukViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: DetailProdukFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DetailProdukFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val argument = arguments?.getSerializable(Constant.produkBundle) as ModelProduk

        setInitValue(argument)

        binding.mbBeli.setOnClickListener {
            val bundle = bundleOf(Constant.produkBundle to argument)
            view.findNavController().navigate(R.id.action_detailProdukFragment_to_dataPembeliFragment, bundle)
        }

    }

    private fun setInitValue(argument: ModelProduk) {
        viewModel.namaProduk.value = argument.nama
        viewModel.hargaProduk.value = argument.harga.toString()
        viewModel.kecamatan.value = argument.kecamatan
        viewModel.kelurahan.value = argument.kelurahan
        viewModel.alamat.value = argument.alamat
        viewModel.stok.value = argument.stok.toString()

        Glide
            .with(requireActivity())
            .load(argument.image)
            .centerCrop()
            .placeholder(R.mipmap.ic_image_placeholder)
            .into(binding.imageProduk)

    }

}