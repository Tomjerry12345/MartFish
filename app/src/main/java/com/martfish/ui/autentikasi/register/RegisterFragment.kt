package com.martfish.ui.autentikasi.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.RegisterFragmentBinding
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar

class RegisterFragment : Fragment(R.layout.register_fragment) {

    private val listJenisAkun = listOf("Pembeli", "Nelayan")
    private val listKecamatan = listOf("Somba Opu", "Samata")
    private val listKelurahan = listOf("Kelurahan 1", "Kelurahan 2")

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModel.Factory(FirestoreDatabase())
    }

    lateinit var binding: RegisterFragmentBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = RegisterFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        dropdown()

        viewModel.response.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {
                    showLogAssert("succes", result.succes)
                    showSnackbar(view, result.succes, "succes")
                    view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(view, result.error, "error")
                }
                is Response.Changed -> {
                    view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            }
        })
    }

    private fun dropdown() {
        val dropdownJenisAkun =  (binding.jenisAkun.editText as? AutoCompleteTextView)
        val dropdownKecamatan =  (binding.kecamatan.editText as? AutoCompleteTextView)
        val dropdownKelurahan =  (binding.kelurahan.editText as? AutoCompleteTextView)

        val jenisAkunAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, listJenisAkun)
        dropdownJenisAkun?.setAdapter(jenisAkunAdapter)
        dropdownJenisAkun?.setOnItemClickListener { adapterView, view, i, l ->
            val getItem = adapterView.getItemAtPosition(i)
            viewModel.jenisAkun.value = getItem as String?
        }

        val kecamatanAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, listKecamatan)
        dropdownKecamatan?.setAdapter(kecamatanAdapter)
        dropdownKecamatan?.setOnItemClickListener { adapterView, view, i, l ->
            val getItem = adapterView.getItemAtPosition(i)
            viewModel.kecamatan.value = getItem as String?
        }

        val kelurahanAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, listKelurahan)
        dropdownKelurahan?.setAdapter(kelurahanAdapter)
        dropdownKelurahan?.setOnItemClickListener { adapterView, view, i, l ->
            val getItem = adapterView.getItemAtPosition(i)
            viewModel.kelurahan.value = getItem as String?
        }
    }

}