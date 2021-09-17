package com.martfish.ui.nelayan.myProduk.editProduk

import android.Manifest
import android.app.Activity
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.martfish.BuildConfig
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.EditProdukNelayanFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar
import java.io.File
import java.io.IOException

class EditProdukNelayanFragment : Fragment(R.layout.edit_produk_nelayan_fragment) {

    private val viewModel: EditProdukNelayanViewModel by viewModels {
        EditProdukNelayanViewModel.Factory(FirestoreDatabase())
    }

    private val listKategori = listOf("Kategori 1", "Kategori 2")

    private lateinit var binding: EditProdukNelayanFragmentBinding

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
        binding = EditProdukNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        activityResultLauncher.launch(Manifest.permission.CAMERA)

        val produk: ModelProduk = arguments?.getSerializable("produk") as ModelProduk

        setValueEditText(produk)
        responseEditProduk(view)
        dropdown()

        binding.mbTambahImage.setOnClickListener {
            showDialogPick()
        }

    }

    private fun setValueEditText(produk: ModelProduk) {
        viewModel.namaProduk.value = produk.nama
        viewModel.hargaProduk.value = produk.harga.toString()
        viewModel.stok.value = produk.stok.toString()
        viewModel.kategori.value = produk.kategori
        viewModel.produk.value = produk
        binding.kategori.editText?.setText(viewModel.kategori.value)

        Glide
            .with(requireActivity())
            .load(produk.image)
            .centerCrop()
            .placeholder(R.mipmap.ic_image_placeholder)
            .into(binding.viewImage)
    }

    private fun responseEditProduk(view: View) {
        viewModel.response.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {
                    showLogAssert("succes", result.succes)
                    showSnackbar(view, result.succes, "succes")
                    view.findNavController().navigate(R.id.action_editProdukNelayanFragment_to_myProdukNelayanFragment)
                }
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(view, result.error, "error")
                }
                is Response.Changed -> TODO()
            }
        })
    }

    private fun dropdown() {
        val dropdownKategori =  (binding.kategori.editText as? AutoCompleteTextView)
        val kategoriAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list, listKategori)
        dropdownKategori?.setAdapter(kategoriAdapter)
        dropdownKategori?.setOnItemClickListener { adapterView, view, i, l ->
            val getItem = adapterView.getItemAtPosition(i)
            viewModel.kategori.value = getItem as String?
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