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
    val hargaPerEkor = MutableLiveData<String>()
    val hargaPerGompo = MutableLiveData<String>()
    val hargaPerKg = MutableLiveData<String>()
    val kategori = MutableLiveData<String>()

    val produk = MutableLiveData<ModelProduk>()

    val response = MutableLiveData<Response>()

    lateinit var dialog: AlertDialog

    fun onEditProduk(view: View) {
        dialog = showDialog(view.context, "Sedang di proses...")
        dialog.show()
        try {
            val imageProduk = imageUri.value ?: throw Exception("Image tidak boleh kosong")
            val kategori = kategori.value ?: throw Exception("Kategori tidak boleh kosong")
            val hargaPerEkor = hargaPerEkor.value ?: throw Exception("Harga/Ekor tidak boleh kosong")
            val hargaPerGompo = hargaPerGompo.value ?: throw Exception("Harga/Gompo tidak boleh kosong")
            val hargaPerKg = hargaPerKg.value ?: throw Exception("Harga/Gompo tidak boleh kosong")

            viewModelScope.launch {
                if (imageUri.value == null) {
//                    val produk = ModelProduk(
//                        idProduk = "",
//                        image = urlImage,
//                        kategori = kategori,
//                        hargaPerEkor = hargaPerEkor.toInt(),
//                        hargaPerGompo = hargaPerGompo.toInt(),
//                        hargaPerKg = hargaPerKg.toInt(),
//                        kecamatan = dataUsers?.kecamatan,
//                        kelurahan = dataUsers?.kelurahan,
//                        alamat = dataUsers?.alamat,
//                        rating = 0f,
//                        namaPenjual = dataUsers?.namaLengkap,
//                        usernamePenjual = dataUsers?.username,
//                    )
                    val produk1 = ModelProduk(
                        idProduk = produk.value?.idProduk,
                        image = produk.value?.image,
                        kategori = kategori,
                        hargaPerEkor = hargaPerEkor.toInt(),
                        hargaPerGompo = hargaPerGompo.toInt(),
                        hargaPerKg = hargaPerKg.toInt(),
                        kecamatan = produk.value?.kecamatan,
                        kelurahan = produk.value?.kelurahan,
                        alamat = produk.value?.alamat,
                        rating = produk.value?.rating,
                        namaPenjual = produk.value?.namaPenjual,
                        usernamePenjual = produk.value?.usernamePenjual
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
                                idProduk = produk.value?.idProduk,
                                image = urlImage,
                                kategori = kategori,
                                hargaPerEkor = hargaPerEkor.toInt(),
                                hargaPerGompo = hargaPerGompo.toInt(),
                                hargaPerKg = hargaPerKg.toInt(),
                                kecamatan = produk.value?.kecamatan,
                                kelurahan = produk.value?.kelurahan,
                                alamat = produk.value?.alamat,
                                rating = produk.value?.rating,
                                namaPenjual = produk.value?.namaPenjual,
                                usernamePenjual = produk.value?.usernamePenjual
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