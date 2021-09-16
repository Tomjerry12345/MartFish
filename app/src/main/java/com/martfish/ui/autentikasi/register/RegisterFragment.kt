package com.martfish.ui.autentikasi.register

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martfish.BuildConfig
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.RegisterFragmentBinding
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar
import java.io.File
import java.io.IOException

class RegisterFragment : Fragment(R.layout.register_fragment) {

    private val listJenisAkun = listOf("Pembeli", "Nelayan")
    private val listKecamatan = listOf("Somba Opu", "Samata")
    private val listKelurahan = listOf("Kelurahan 1", "Kelurahan 2")

    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModel.Factory(FirestoreDatabase())
    }

    lateinit var binding: RegisterFragmentBinding

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

    private var latestTmpUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = RegisterFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        activityResultLauncher.launch(Manifest.permission.CAMERA)

        binding.mbTambahImage.setOnClickListener {
            showDialogPick()
        }

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