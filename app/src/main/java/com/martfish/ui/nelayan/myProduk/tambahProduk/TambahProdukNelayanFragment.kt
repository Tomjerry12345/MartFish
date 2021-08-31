package com.martfish.ui.nelayan.myProduk.tambahProduk

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.TambahProdukNelayanFragmentBinding
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar
import java.io.IOException

class TambahProdukNelayanFragment : Fragment(R.layout.tambah_produk_nelayan_fragment) {

    private val viewModel: TambahProdukNelayanViewModel by viewModels {
        TambahProdukNelayanViewModel.Factory(FirestoreDatabase())
    }
    private lateinit var binding: TambahProdukNelayanFragmentBinding
    private val listKategori = listOf("Kategori 1", "Kategori 2")

    private val PICK_IMAGE_REQUEST = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TambahProdukNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

//        SavedData.init(requireActivity())

        binding.mbTambahImage.setOnClickListener {
            getImageFromStorage()
        }

        dropdown()
        responseCreateProduk(view)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            viewModel.imageUri.value = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, viewModel.imageUri.value)
                binding.viewImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
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

    private fun responseCreateProduk(view: View) {
        viewModel.response.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {
                    showLogAssert("succes", result.succes)
                    showSnackbar(view, result.succes, "succes")
//                    viewModel.imageUri.value = null
//                    binding.viewImage.setImageResource(R.drawable.ic_baseline_image_24)
//                    viewModel.namaProduk.value = ""
//                    viewModel.hargaProduk.value = ""
//                    viewModel.stok.value = ""
//                    viewModel.kategori.value = ""
//                    binding.kategori.editText?.setText("")
                    view.findNavController().navigate(R.id.action_tambahProdukNelayanFragment_to_myProdukNelayanFragment)
                }
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(view, result.error, "error")
                }
                is Response.Changed -> TODO()
            }
        })
    }

    private fun getImageFromStorage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

}