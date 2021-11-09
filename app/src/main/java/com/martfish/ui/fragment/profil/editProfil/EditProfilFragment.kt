package com.martfish.ui.fragment.profil.editProfil

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martfish.BuildConfig
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.EditProfilFragmentBinding
import com.martfish.utils.*
import java.io.File
import java.io.IOException

class EditProfilFragment : Fragment(R.layout.edit_profil_fragment) {

    private val viewModel: EditProfilViewModel by viewModels {
        EditProfilViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: EditProfilFragmentBinding

    private val kelurahan = MutableLiveData<List<String>>()

    private val dataUsers = SavedData.getDataUsers()

//    private val listKecamatan = listOf("Somba Opu", "Samata")
//    private val listKelurahan = listOf("Kelurahan 1", "Kelurahan 2")

    private val getFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.imageUri.value = uri

        if (viewModel.imageUri.value != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(
                    activity?.contentResolver,
                    viewModel.imageUri.value
                )
                binding.viewImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                showLogAssert("error", "${e.message}")
            }

        }

    }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    viewModel.imageUri.value = uri
                    binding.viewImage.setImageURI(uri)
                }
            }
        }

    private var latestTmpUri: Uri? = null

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                showSnackbar(requireView(), "isGranted", "succes")
            } else {
                showSnackbar(requireView(), "denied", "error")
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditProfilFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        activityResultLauncher.launch(Manifest.permission.CAMERA)

        initSetValue()
        dropdown()
        response()

        binding.mbTambahImage.setOnClickListener {
            showDialogPick()
        }

    }

    private fun initSetValue() {
        Glide.with(requireContext())
            .load(dataUsers?.image)
            .centerCrop()
            .into(binding.viewImage)

        viewModel.namaLengkap.value = dataUsers?.namaLengkap
        binding.kecamatan.editText?.setText(dataUsers?.kecamatan)
        binding.kelurahan.editText?.setText(dataUsers?.kelurahan)
        viewModel.kecamatan.value = dataUsers?.kecamatan
        viewModel.kelurahan.value = dataUsers?.kelurahan
        viewModel.alamat.value = dataUsers?.alamat
        viewModel.noHp.value = dataUsers?.noHp
        viewModel.dataUsers.value = dataUsers
    }

    private fun dropdown() {
        val dropdownKecamatan = (binding.kecamatan.editText as? AutoCompleteTextView)
        val dropdownKelurahan = (binding.kelurahan.editText as? AutoCompleteTextView)

        binding.kelurahan.isEnabled = false

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

        viewModel.kecamatan.observe(viewLifecycleOwner, {
            binding.kelurahan.isEnabled = it != null
        })

        kelurahan.observe(viewLifecycleOwner, {
            val kelurahanAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, it)
            dropdownKelurahan?.setAdapter(kelurahanAdapter)
            dropdownKelurahan?.setOnItemClickListener { adapterView, view, i, l ->
                val getItem = adapterView.getItemAtPosition(i)
                viewModel.kelurahan.value = getItem as String?
            }
        })


    }

    private fun response() {
        viewModel.response.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {
                    showLogAssert("succes", result.succes)

                    if (dataUsers?.jenisAkun == "Nelayan") {
                        showLogAssert("msg", "Nelayan")
                        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                    }
                    else {
                        showLogAssert("msg", "Petani")
                        findNavController().navigate(R.id.action_editProfilFragment2_to_profileFragment)
                    }
                }
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(requireView(), result.error, "error")
                }
                is Response.Changed -> {
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDialogPick() {
        val items = arrayOf("Camera", "Gallery")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih aksi")
            .setItems(items) { dialog, which ->
                if (which == 0)
                    capturePhoto()
                else
                    getImageFromStorage()
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun capturePhoto() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    private fun getImageFromStorage() {
        getFromGallery.launch("image/*")
    }

}