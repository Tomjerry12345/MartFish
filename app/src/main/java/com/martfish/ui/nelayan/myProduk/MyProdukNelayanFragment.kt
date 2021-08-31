package com.martfish.ui.nelayan.myProduk

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.MyProdukNelayanFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.ui.nelayan.myProduk.adapter.MyProdukAdapter
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar

class MyProdukNelayanFragment : Fragment(R.layout.my_produk_nelayan_fragment) {

    private val viewModel: MyProdukNelayanViewModel by viewModels {
        MyProdukNelayanViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: MyProdukNelayanFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MyProdukNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        getAllDataProduk(view)
        deleteProduk(view)

        binding.floatingTambah.setOnClickListener {
            view.findNavController().navigate(R.id.action_myProdukNelayanFragment_to_tambahProdukNelayanFragment)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllDataProduk(view: View) {
        viewModel.data.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {}
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(view, result.error, "error")
                }
                is Response.Changed -> {
                    val data = result.data as QuerySnapshot
                    val dataProduk: List<ModelProduk> = data.toObjects()
                    val myProdukAdapter = MyProdukAdapter(dataProduk, viewModel)
                    binding.rvListProduk.apply {
                        layoutManager = GridLayoutManager(requireActivity(), 2)
                        adapter = myProdukAdapter
                    }
                    myProdukAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun deleteProduk(view: View) {
        viewModel.response.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {
                    showSnackbar(view, result.succes, "success")
                    view.findNavController().navigate(R.id.action_myProdukNelayanFragment_self)
                }
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(view, result.error, "error")
                }
                is Response.Changed -> { }
            }
        })

    }

}