package com.martfish.ui.nelayan.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.martfish.R
import com.martfish.databinding.HomeNelayanFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.ui.adapter.ProdukAdapter
import com.martfish.utils.showLogAssert

class HomeNelayanFragment : Fragment(R.layout.home_nelayan_fragment) {


    private val viewModel: HomeNelayanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val binding = HomeNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

//        val listProduk = listOf(
//            ModelProduk("", "Ikan 1", 200000, "Kecamatan 1", 50f),
//            ModelProduk("", "Ikan 2", 100000, "", 30f),
//            ModelProduk("", "Ikan 3", 300000, "", 40f),
//        )

//        val produkAdapter = ProdukAdapter(listProduk)

//        binding.rvListProduk.apply {
//            layoutManager = GridLayoutManager(requireActivity(), 2)
//            adapter = produkAdapter
//        }
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

}