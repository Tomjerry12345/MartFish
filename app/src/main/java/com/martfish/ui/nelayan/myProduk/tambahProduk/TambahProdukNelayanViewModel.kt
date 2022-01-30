package com.martfish.ui.nelayan.myProduk.tambahProduk

import android.app.AlertDialog
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelProduk
import com.martfish.utils.*
import kotlinx.coroutines.launch

class TambahProdukNelayanViewModel(private val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val imageUri = MutableLiveData<Uri>()
    val hargaPerEkor = MutableLiveData<String>()
    val hargaPerGompo = MutableLiveData<String>()
//    val stok = MutableLiveData<String>()
    val hargaPerKg = MutableLiveData<String>()
    val kategori = MutableLiveData<String>()

    val dataUsers = SavedData.getDataUsers()
    val response = MutableLiveData<Response>()

    lateinit var dialog: AlertDialog

    init {
        showLogAssert("dataUsers", "$dataUsers")
    }

    fun onTambahProduk(view: View) {
        dialog = showDialog(view.context, "Sedang di proses...")
        dialog.show()
        try {
            val imageProduk = imageUri.value ?: throw Exception("Image tidak boleh kosong")
            val kategori = kategori.value ?: throw Exception("Kategori tidak boleh kosong")
            val hargaPerEkor = hargaPerEkor.value ?: throw Exception("Harga/Ekor tidak boleh kosong")
            val hargaPerGompo = hargaPerGompo.value ?: throw Exception("Harga/Gompo tidak boleh kosong")
            val hargaPerKg = hargaPerKg.value ?: throw Exception("Harga/Gompo tidak boleh kosong")

            viewModelScope.launch {
                when (val getUrlImage = firestoreDatabase.uploadPhoto(imageProduk, "images/produk/")) {
                    is Response.Changed -> {
                        val urlImage = getUrlImage.data as String
                        val produk = ModelProduk(
                            idProduk = "",
                            image = urlImage,
                            kategori = kategori,
                            hargaPerEkor = hargaPerEkor.toInt(),
                            hargaPerGompo = hargaPerGompo.toInt(),
                            hargaPerKg = hargaPerKg.toInt(),
                            kecamatan = dataUsers?.kecamatan,
                            kelurahan = dataUsers?.kelurahan,
                            alamat = dataUsers?.alamat,
                            rating = 0f,
                            namaPenjual = dataUsers?.namaLengkap,
                            usernamePenjual = dataUsers?.username,
                        )

                        saveProduk(produk)

                    }

                    is Response.Error -> {
                        showLogAssert("error", getUrlImage.error)
                    }

                    is Response.Success -> {
                    }
                }

                dialog.dismiss()

            }

        } catch (e: Exception) {
            e.message?.let { showSnackbar(view, it, "error") }
            dialog.dismiss()
        }
    }

    private suspend fun saveProduk(produk: ModelProduk) {
        when (val getResponse =
            firestoreDatabase.saveDataReference("produk", produk)) {
            is Response.Changed -> {
                response.value = firestoreDatabase.updateReferenceCollectionOne(
                    "produk",
                    getResponse.data.toString(),
                    "idProduk",
                    getResponse.data.toString(),
                    "Tambah Produk Berhasil"
                )
            }

            is Response.Error -> {
                showLogAssert("error", getResponse.error)
            }

            is Response.Success -> {
            }
        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TambahProdukNelayanViewModel(firestoreDatabase) as T
        }
    }

}