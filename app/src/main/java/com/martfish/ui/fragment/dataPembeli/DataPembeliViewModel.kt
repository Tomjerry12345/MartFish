package com.martfish.ui.fragment.dataPembeli

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelPemesanan
import com.martfish.ui.activity.pembayaran.PembayaranActivity
import com.martfish.ui.activity.succes.SuccesActivity
import com.martfish.utils.Constant
import com.martfish.utils.SavedData
import com.martfish.utils.showSnackbar

@SuppressLint("StaticFieldLeak")
class DataPembeliViewModel(
    val firestoreDatabase: FirestoreDatabase,
    val activity: FragmentActivity
) : ViewModel() {

    val namaPenerima = MutableLiveData<String>()
    val kecamatan = MutableLiveData<String>()
    val kelurahan = MutableLiveData<String>()
    val alamat = MutableLiveData<String>()
    val jumlahBeli = MutableLiveData<String>()
    val harga = MutableLiveData<String>()
    val totalBayar = MutableLiveData<String>()
    val namaProduk = MutableLiveData<String>()

    var total = 0
    var hargaProduk = 0
    var usernamePenjual = ""
    var metodePembayaran: String? = null
    var image = ""
    var idProduk = ""

    private val dataUser = SavedData.getDataUsers()

    fun onKonfirmasi(view: View) {
        try {
            val namaPenerima = namaPenerima.value ?: throw Exception("Nama Penerima tidak boleh kosong")
            val kecamatan = kecamatan.value ?: throw Exception("Kecamatan tidak boleh kosong")
            val kelurahan = kelurahan.value ?: throw Exception("Kelurahan tidak boleh kosong")
            val alamat = alamat.value ?: throw Exception("ALamat tidak boleh kosong")
            val metodePembayaran1 = metodePembayaran ?: throw Exception("Metode pembayaran tidak boleh kosong")

            val pemesan = ModelPemesanan(
                namaPenerima,
                namaProduk.value,
                jumlahBeli.value?.toInt(),
                hargaProduk.toDouble(),
                total.toDouble(),
                kecamatan,
                kelurahan,
                alamat,
                dataUser?.username,
                usernamePenjual,
                false,
                image,
                null,
                idProduk
            )

            if (metodePembayaran1 == "cod") {
                val intent = Intent(activity, SuccesActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            } else {
                val intent = Intent(activity, PembayaranActivity::class.java)
                intent.putExtra(Constant.listDataPembeliBundle, pemesan)
                activity.startActivity(intent)
            }

        } catch (e: Exception) {
            showSnackbar(view, "${e.message}", "error")
        }

    }

    fun onTambah() {
        jumlahBeli.value = jumlahBeli.value?.toInt()?.plus(1).toString()
    }

    fun onKurang() {
        if (jumlahBeli.value?.toInt()!! > 1)
            jumlahBeli.value = jumlahBeli.value?.toInt()?.minus(1).toString()
    }

    class Factory(
        private val firestoreDatabase: FirestoreDatabase,
        private val activity: FragmentActivity
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DataPembeliViewModel(firestoreDatabase, activity) as T
        }
    }
}