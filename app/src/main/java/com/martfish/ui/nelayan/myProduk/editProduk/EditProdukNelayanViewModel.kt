package com.martfish.ui.nelayan.myProduk.editProduk

import android.app.AlertDialog
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelProduk
import com.martfish.utils.Response
import com.martfish.utils.showDialog
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar
import kotlinx.coroutines.launch

class EditProdukNelayanViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val imageUri = MutableLiveData<Uri>()
    val namaProduk = MutableLiveData<String>()
    val hargaProduk = MutableLiveData<String>()
    val stok = MutableLiveData<String>()
    val kategori = MutableLiveData<String>()

    val produk = MutableLiveData<ModelProduk>()

    val response = MutableLiveData<Response>()

    lateinit var dialog: AlertDialog

    fun onEditProduk(view: View) {
        dialog = showDialog(view.context, "Sedang di proses...")
        dialog.show()
        try {
            val namaProduk = namaProduk.value ?: throw Exception("Nama Produk tidak boleh kosong")
            val hargaProduk = hargaProduk.value ?: throw Exception("Harga tidak boleh kosong")
            val stok = stok.value ?: throw Exception("Stok tidak boleh kosong")
            val kategori = kategori.value ?: throw Exception("Kategori tidak boleh kosong")

            viewModelScope.launch {
                if (imageUri.value == null) {
                    val produk1 = ModelProduk(
                        produk.value?.idProduk,
                        produk.value?.image,
                        namaProduk,
                        hargaProduk.toInt(),
                        stok.toInt(),
                        kategori,
                        produk.value?.kecamatan,
                        produk.value?.kelurahan,
                        produk.value?.alamat,
                        0f,
                        produk.value?.namaPenjual,
                        produk.value?.usernamePenjual
                    )

                    response.value = produk.value?.idProduk?.let {
                        firestoreDatabase.updateReferenceCollectionOne(
                            "produk",
                            it, null, produk1
                        )
                    }

                    dialog.dismiss()
                } else {
                    when (val getUrlImage =
                        firestoreDatabase.uploadPhoto(imageUri.value!!, "images/produk/")) {
                        is Response.Changed -> {
                            val urlImage = getUrlImage.data as String
                            val produk1 = ModelProduk(
                                produk.value?.idProduk,
                                urlImage,
                                namaProduk,
                                hargaProduk.toInt(),
                                stok.toInt(),
                                kategori,
                                produk.value?.kecamatan,
                                produk.value?.kelurahan,
                                produk.value?.alamat,
                                0f,
                                produk.value?.namaPenjual,
                                produk.value?.usernamePenjual
                            )

                            response.value = produk.value?.idProduk?.let {
                                firestoreDatabase.updateReferenceCollectionOne(
                                    "produk",
                                    it, null, produk1
                                )
                            }

                            dialog.dismiss()

                        }

                        is Response.Error -> {
                            showLogAssert("error", getUrlImage.error)
                            dialog.dismiss()
                        }

                        is Response.Success -> {
                        }
                    }
                }

            }

        } catch (e: Exception) {
            e.message?.let { showSnackbar(view, it, "error") }
            dialog.dismiss()
        }

    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditProdukNelayanViewModel(firestoreDatabase) as T
        }
    }

}