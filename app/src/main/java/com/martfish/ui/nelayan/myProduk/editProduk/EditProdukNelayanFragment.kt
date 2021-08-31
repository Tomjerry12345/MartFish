package com.martfish.ui.nelayan.myProduk.editProduk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.EditProdukNelayanFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar
import java.io.IOException

class EditProdukNelayanFragment : Fragment(R.layout.edit_produk_nelayan_fragment) {

    private val viewModel: EditProdukNelayanViewModel by viewModels {
        EditProdukNelayanViewModel.Factory(FirestoreDatabase())
    }

    private val listKategori = listOf("Kategori 1", "Kategori 2")

    private val PICK_IMAGE_REQUEST = 1

    private lateinit var binding: EditProdukNelayanFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditProdukNelayanFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val produk: ModelProduk = arguments?.getSerializable("produk") as ModelProduk

        setValueEditText(produk)
        responseEditProduk(view)
        dropdown()

        binding.mbTambahImage.setOnClickListener {
            getImageFromStorage()
        }

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

    private fun getImageFromStorage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

}