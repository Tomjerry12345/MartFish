package com.martfish.ui.autentikasi.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.martfish.R
import com.martfish.databinding.RegisterFragmentBinding

class RegisterFragment : Fragment(R.layout.register_fragment) {

    private lateinit var viewModel: RegisterViewModel
    private val listJenisAkun = listOf("Pembeli", "Nelayan")
    private val listKecamatan = listOf("Somba Opu", "Samata")
    private val listKelurahan = listOf("Kelurahan 1", "Kelurahan 2")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        val binding = RegisterFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val dropdownJenisAkun =  (binding.jenisAkun.editText as? AutoCompleteTextView)
        val dropdownKelurahan =  (binding.kecamatan.editText as? AutoCompleteTextView)
        val dropdownKecamatan =  (binding.kelurahan.editText as? AutoCompleteTextView)

        val jenisAkunAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, listJenisAkun)
        dropdownJenisAkun?.setAdapter(jenisAkunAdapter)

        val kecamatanAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, listKecamatan)
        dropdownKelurahan?.setAdapter(kecamatanAdapter)
        val kelurahanAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, listKelurahan)
        dropdownKecamatan?.setAdapter(kelurahanAdapter)
    }

}