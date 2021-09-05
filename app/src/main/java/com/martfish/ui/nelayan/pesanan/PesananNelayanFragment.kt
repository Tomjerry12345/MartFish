package com.martfish.ui.nelayan.pesanan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.martfish.R
import com.martfish.databinding.PesananNelayanFragmentBinding
import com.martfish.ui.adapter.TabAdapter

class PesananNelayanFragment : Fragment(R.layout.pesanan_nelayan_fragment) {

    private val viewModel: PesananNelayanViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = PesananNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val listTitle = arrayOf("Pesanan belum terkirim", "Pesanan telah terkirim")

        val adapter = TabAdapter(this)

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            tab.text = listTitle[position]
        }.attach()

    }

}