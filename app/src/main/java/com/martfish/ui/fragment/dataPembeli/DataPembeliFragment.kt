package com.martfish.ui.fragment.dataPembeli

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
            ActivityResultContracts.RequestMultiplePermissions())
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataPembeliFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        activityResultLauncher.launch(
            arrayOf(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION)
        )

        getLokasi()
        setInitValue()
        dropdown()
        totalBayar()
        radioMetodePembayaran()

    }

    private fun getLokasi() {
        permissionMaps.observe(viewLifecycleOwner, { permission ->
            Maps.initMaps(requireActivity())
            Maps.getDeviceLocation(permission).observe(viewLifecycleOwner, {
                if (it != null) {
                    viewModel.latitude.value = it["latitude"]!!
                    viewModel.longitude.value = it["longitude"]!!
                    showLogAssert("latitude", "${viewModel.latitude.value}")
                    showLogAssert("longitude", "${viewModel.longitude.value}")
                } else {
                    showLogAssert("null maps.location", "null maps.location")
                    showSnackbar(binding.root, "Error lokasi tidak di temukan", "error")
                }
            })
        })
    }

    private fun setInitValue() {
        argument = SavedData.getDataProduk()!!

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
    }

    private fun dropdown() {
        val dropdownKecamatan =  (binding.kecamatan.editText as? AutoCompleteTextView)
        val dropdownKelurahan =  (binding.kelurahan.editText as? AutoCompleteTextView)

        val kecamatanAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, Constant.listKecamatan)
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

        kelurahan.observe(viewLifecycleOwner, {
            val kelurahanAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, it)
            dropdownKelurahan?.setAdapter(kelurahanAdapter)
            dropdownKelurahan?.setOnItemClickListener { adapterView, view, i, l ->
                val getItem = adapterView.getItemAtPosition(i)
                viewModel.kelurahan.value = getItem as String?
            }
        })


    }

    private fun radioMetodePembayaran() {
        binding.radioGroupPembayaran.setOnCheckedChangeListener { radioGroup, i ->
            when(i) {
                R.id.cod -> {
                    viewModel.metodePembayaran = "cod"
                }

                R.id.lainnya -> {
                    viewModel.metodePembayaran = "lainnya"
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