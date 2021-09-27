package com.martfish.ui.nelayan.myProduk.tambahProduk

import android.Manifest.permission.CAMERA
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martfish.BuildConfig
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.TambahProdukNelayanFragmentBinding
import com.martfish.utils.Constant.listKategori
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar
import java.io.File
import java.io.IOException

class TambahProdukNelayanFragment : Fragment(R.layout.tambah_produk_nelayan_fragment) {

    private val viewModel: TambahProdukNelayanViewModel by viewModels {
        TambahProdukNelayanViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: TambahProdukNelayanFragmentBinding

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
        binding = TambahProdukNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        activityResultLauncher.launch(CAMERA)

        binding.mbTambahImage.setOnClickListener {
            showDialogPick()
        }

        dropdown()
        responseCreateProduk(view)

    }

    private fun dropdown() {
        val dropdownKategori =  (binding.kategori.editText as? AutoCompleteTextView)
        val kategoriAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, listKategori)
        dropdownKategori?.setAdapter(kategoriAdapter)
        dropdownKategori?.setOnItemClickListener { adapterView, _, i, _ ->
            val getItem = adapterView.getItemAtPosition(i)
            viewModel.kategori.value = getItem as String?
        }

    }

    private fun responseCreateProduk(view: View) {
        viewModel.response.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {
                    showLogAssert("succes", result.succes)
                    showSnackbar(view, result.succes, "succes")
                    view.findNavController()
                        .navigate(R.id.action_tambahProdukNelayanFragment_to_myProdukNelayanFragment)
                }
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(view, result.error, "error")
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