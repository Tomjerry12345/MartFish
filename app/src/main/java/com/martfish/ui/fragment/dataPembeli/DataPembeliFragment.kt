package com.martfish.ui.fragment.dataPembeli

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.DataPembeliFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.ui.activity.succes.SuccesActivity
import com.martfish.utils.Constant
import com.martfish.utils.SavedData
import com.martfish.utils.showSnackbar

class DataPembeliFragment : Fragment(R.layout.data_pembeli_fragment) {

    private val viewModel: DataPembeliViewModel by viewModels {
        DataPembeliViewModel.Factory(FirestoreDatabase(), requireActivity())
    }

    private lateinit var binding: DataPembeliFragmentBinding
    private lateinit var argument: ModelProduk

    private val listKecamatan = listOf("Somba Opu", "Samata")
    private val listKelurahan = listOf("Kelurahan 1", "Kelurahan 2")

    val dataUsers = SavedData.getDataUsers()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataPembeliFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        argument = arguments?.getSerializable(Constant.produkBundle) as ModelProduk

        viewModel.namaPenerima.value = dataUsers?.namaLengkap
        viewModel.kecamatan.value = dataUsers?.kecamatan
        viewModel.kelurahan.value = dataUsers?.kelurahan
        viewModel.alamat.value = dataUsers?.alamat
        viewModel.jumlahBeli.value = 1.toString()
        viewModel.harga.value ="harga: ${argument.harga.toString()}"
        viewModel.namaProduk.value = argument.nama
        viewModel.hargaProduk = argument.harga!!
        viewModel.usernamePenjual = argument.usernamePenjual.toString()
        viewModel.image = argument.image.toString()
        viewModel.idProduk = argument.idProduk.toString()

        binding.kecamatan.editText?.setText(viewModel.kecamatan.value)
        binding.kelurahan.editText?.setText(viewModel.kelurahan.value)

        dropdown()
        totalBayar()
        radioMetodePembayaran()

    }

    private fun dropdown() {
        val dropdownKecamatan =  (binding.kecamatan.editText as? AutoCompleteTextView)
        val dropdownKelurahan =  (binding.kelurahan.editText as? AutoCompleteTextView)

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

    private fun radioMetodePembayaran() {
        binding.radioGroupPembayaran.setOnCheckedChangeListener { radioGroup, i ->
            when(i) {
                R.id.cod -> {
                    showSnackbar(requireView(), "cod", "succes")
                    viewModel.metodePembayaran = "cod"
                    val intent = Intent(requireActivity(), SuccesActivity::class.java)
                    startActivity(intent)
                }

                R.id.lainnya -> {
                    viewModel.metodePembayaran = "lainnya"
                    showSnackbar(requireView(), "lainnya", "succes")
                }
            }
        }
    }

    private fun totalBayar() {
        viewModel.jumlahBeli.observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.total = (it.toInt() * argument.harga!!)
                viewModel.totalBayar.value = "Total bayar : ${viewModel.total}"
            }
        })
    }
}