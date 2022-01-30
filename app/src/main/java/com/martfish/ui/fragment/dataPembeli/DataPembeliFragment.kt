package com.martfish.ui.fragment.dataPembeli

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.DataPembeliFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.ui.activity.succes.SuccesActivity
import com.martfish.utils.*
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import kotlin.properties.Delegates

class DataPembeliFragment : Fragment(R.layout.data_pembeli_fragment) {

    private val viewModel: DataPembeliViewModel by viewModels {
        DataPembeliViewModel.Factory(FirestoreDatabase(), requireActivity())
    }

    private lateinit var binding: DataPembeliFragmentBinding
    private lateinit var argument: ModelProduk

    private val kelurahan = MutableLiveData<List<String>>()

//    private val listKecamatan = listOf("Somba Opu", "Samata")
//    private val listKelurahan = listOf("Kelurahan 1", "Kelurahan 2")

    val dataUsers = SavedData.getDataUsers()

    private val permissionMaps = MutableLiveData<Boolean>()

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                showLogAssert("permissionName", permissionName)
                showLogAssert("isGranted", "$isGranted")
                if (permissionName == Manifest.permission.ACCESS_FINE_LOCATION)
                    permissionMaps.value = isGranted
            }
        }

    private lateinit var listHarga: List<String>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataPembeliFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        activityResultLauncher.launch(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )



        getLokasi()
        setInitValue()
        dropdown()
        totalBayar()
        radioMetodePembayaran()
        radioStatusPengantaran()

    }

    private fun getLokasi() {
        permissionMaps.observe(viewLifecycleOwner) { permission ->
            Maps.initMaps(requireActivity())
            Maps.getDeviceLocation(permission).observe(viewLifecycleOwner) {
                if (it != null) {
                    viewModel.latitude.value = it["latitude"]!!
                    viewModel.longitude.value = it["longitude"]!!
                    showLogAssert("latitude", "${viewModel.latitude.value}")
                    showLogAssert("longitude", "${viewModel.longitude.value}")
                } else {
                    showLogAssert("null maps.location", "null maps.location")
                    showSnackbar(binding.root, "Error lokasi tidak di temukan", "error")
                }
            }
        }
    }

    private fun setInitValue() {
        argument = SavedData.getDataProduk()!!

        viewModel.namaPenerima.value = dataUsers?.namaLengkap
        viewModel.kecamatan.value = dataUsers?.kecamatan
        viewModel.kelurahan.value = dataUsers?.kelurahan
        viewModel.alamat.value = dataUsers?.alamat
        viewModel.jumlahBeli.value = 1.toString()
        viewModel.namaProduk.value = argument.kategori
        viewModel.usernamePenjual = argument.usernamePenjual.toString()
        viewModel.image = argument.image.toString()
        viewModel.idProduk = argument.idProduk.toString()

        binding.kecamatan.editText?.setText(viewModel.kecamatan.value)
        binding.kelurahan.editText?.setText(viewModel.kelurahan.value)

        listHarga = listOf(
            "${argument.hargaPerEkor}/Ekor",
            "${argument.hargaPerGompo}/Gompo",
            "${argument.hargaPerKg}/Kg",
        )
    }

    private fun dropdown() {
        val dropdownKecamatan = (binding.kecamatan.editText as? AutoCompleteTextView)
        val dropdownKelurahan = (binding.kelurahan.editText as? AutoCompleteTextView)
        val dropdownHarga = (binding.harga.editText as? AutoCompleteTextView)

        binding.kelurahan.isEnabled = false

        val kecamatanAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_list, Constant.listKecamatan)
        dropdownKecamatan?.setAdapter(kecamatanAdapter)
        dropdownKecamatan?.setOnItemClickListener { adapterView, view, i, l ->
            val getItem = adapterView.getItemAtPosition(i)
            viewModel.kecamatan.value = getItem as String?
            when (viewModel.kecamatan.value) {
                "Bajeng" -> kelurahan.value = Constant.kelurahanBajeng
                "Barombong" -> kelurahan.value = Constant.kelurahanBarombong
                "Biringbulu" -> kelurahan.value = Constant.kelurahanBiringbulu
                "Bontomarannu" -> kelurahan.value = Constant.kelurahanBontomarannu
                "Bontonompo" -> kelurahan.value = Constant.kelurahanBontonompo
                "Bontonompo Selatan" -> kelurahan.value = Constant.kelurahanBontonompoSelatan
                "Bungaya" -> kelurahan.value = Constant.kelurahanBungaya
                "Pallangga" -> kelurahan.value = Constant.kelurahanPallangga
                "Parangloe" -> kelurahan.value = Constant.kelurahanParangloe
                "Somba Opu" -> kelurahan.value = Constant.kelurahanSombaOpu
                "Tinggimoncong" -> kelurahan.value = Constant.kelurahanTinggimoncong
                "Tompobulu" -> kelurahan.value = Constant.kelurahanTompobulu
                "Tombolo Pao" -> kelurahan.value = Constant.kelurahanTomboloPao
            }
        }

        viewModel.kecamatan.observe(viewLifecycleOwner) {
            binding.kelurahan.isEnabled = it != null
        }

        kelurahan.observe(viewLifecycleOwner) {
            val kelurahanAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, it)
            dropdownKelurahan?.setAdapter(kelurahanAdapter)
            dropdownKelurahan?.setOnItemClickListener { adapterView, view, i, l ->
                val getItem = adapterView.getItemAtPosition(i)
                viewModel.kelurahan.value = getItem as String?
            }
        }

        val hargaAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, listHarga)
        dropdownHarga?.setAdapter(hargaAdapter)
        dropdownHarga?.setOnItemClickListener { adapterView, view, i, l ->
            val getItem = adapterView.getItemAtPosition(i)
            viewModel.kategoriHarga.value = getItem as String?
            when (viewModel.kategoriHarga.value) {
                listHarga[0] -> {
                    viewModel.totalBayar.value = "Total bayar : ${argument.hargaPerEkor}"
                    Toast.makeText(requireContext(), "hargaPerEkor", Toast.LENGTH_LONG).show()
                }
                listHarga[1] -> {
                    viewModel.totalBayar.value = "Total bayar : ${argument.hargaPerGompo}"
                    Toast.makeText(requireContext(), "hargaPerGompo", Toast.LENGTH_LONG).show()
                }
                else -> {
                    viewModel.totalBayar.value = "Total bayar : ${argument.hargaPerKg}"
                    Toast.makeText(requireContext(), "hargaPerKg", Toast.LENGTH_LONG).show()
                }
            }

        }


    }

    private fun radioMetodePembayaran() {
        binding.radioGroupPembayaran.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.cod -> {
                    viewModel.metodePembayaran = "cod"
                }

                R.id.lainnya -> {
                    viewModel.metodePembayaran = "lainnya"
                }
            }
        }
    }

    private fun radioStatusPengantaran() {
        binding.radioGroupMetodePengantaran.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.ambilSendiri -> {
                    viewModel.metodePengantaran = "ambil sendiri"
                }

                R.id.kurir -> {
                    viewModel.metodePengantaran = "kurir"
                }
            }
        }
    }

    private fun totalBayar() {
        viewModel.jumlahBeli.observe(viewLifecycleOwner) {
            if (it != null) {
                when (viewModel.kategoriHarga.value) {
                    listHarga[0] -> {
                        viewModel.total = (it.toInt() * argument.hargaPerEkor!!)
                        viewModel.totalBayar.value = "Total bayar : ${viewModel.total}"
                        Toast.makeText(requireContext(), "hargaPerEkor", Toast.LENGTH_LONG).show()
                    }
                    listHarga[1] -> {
                        viewModel.total = (it.toInt() * argument.hargaPerGompo!!)
                        viewModel.totalBayar.value = "Total bayar : ${viewModel.total}"
                        Toast.makeText(requireContext(), "hargaPerGompo", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        viewModel.total = (it.toInt() * argument.hargaPerKg!!)
                        viewModel.totalBayar.value = "Total bayar : ${viewModel.total}"
                        Toast.makeText(requireContext(), "hargaPerKg", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }
    }
}