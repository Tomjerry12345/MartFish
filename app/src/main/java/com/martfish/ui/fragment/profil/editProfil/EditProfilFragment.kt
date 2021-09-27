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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martfish.BuildConfig
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.EditProfilFragmentBinding
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar
import java.io.File
import java.io.IOException

class EditProfilFragment : Fragment(R.layout.edit_profil_fragment) {

    private val viewModel: EditProfilViewModel by viewModels {
        EditProfilViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: EditProfilFragmentBinding

    private val dataUsers = SavedData.getDataUsers()

    private val listKecamatan = listOf("Somba Opu", "Samata")
    private val listKelurahan = listOf("Kelurahan 1", "Kelurahan 2")

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

    private fun response() {
        viewModel.response.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {
                    showLogAssert("succes", result.succes)

                    if (dataUsers?.jenisAkun == "Nelayan")
                        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                    else
                        findNavController().navigate(R.id.action_editProfilFragment2_to_profileFragment)
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