package com.martfish.ui.nelayan.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.HomeNelayanFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.ui.adapter.ProdukAdapter
import com.martfish.ui.nelayan.myProduk.adapter.MyProdukAdapter
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar

class HomeNelayanFragment : Fragment(R.layout.home_nelayan_fragment) {

    private val viewModel: HomeNelayanViewModel by viewModels {
        HomeNelayanViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: HomeNelayanFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding = HomeNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        getAllDataProduk()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)
        val searchItem = menu.findItem(R.id.search_toolbar)

        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { showLogAssert("onQueryTextSubmit", it) }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { showLogAssert("onQueryTextChange", it) }
                return true
            }

        })
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