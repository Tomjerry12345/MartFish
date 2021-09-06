package com.martfish.ui.fragment.komentar

import android.app.AlertDialog
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelKomentar
import com.martfish.model.ModelProduk
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import com.martfish.utils.showDialog
import com.martfish.utils.showLogAssert
import kotlinx.coroutines.launch

class KomentarViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {
    val komentar = MutableLiveData<String>()

    var modelProduk: ModelProduk? = null
    val dataUsers = SavedData.getDataUsers()

    var rating = 0.0

    lateinit var dialog: AlertDialog

    fun sendKomentar(view: View) {

        dialog = showDialog(view.context, "Sedang di proses...")
        dialog.show()

        val komentar1 = ModelKomentar(
            null,
            null,
            dataUsers?.namaLengkap,
            komentar.value,
            rating
        )
        viewModelScope.launch {
            updateRating()
            saveKomentar(komentar1)
            modelProduk?.rating = rating.toFloat()
            modelProduk?.let { SavedData.saveDataProduk(it) }
            view.findNavController().navigate(
                R.id.action_komentarFragment_to_detailProdukFragment
            )
        }
    }

    private suspend fun updateRating() {
        val sumRating = modelProduk?.rating?.plus(rating)
        if (sumRating != null) {
            firestoreDatabase.updateReferenceCollectionOne(
                "produk",
                modelProduk?.idProduk.toString(),
                "rating",
                sumRating
            )
            rating = sumRating
        }
    }

    private suspend fun saveKomentar(komentar1: ModelKomentar) {
        val response = firestoreDatabase.saveDataReferenceDocumentOne(
            "komentar",
            modelProduk?.idProduk.toString(), komentar1
        )

        when (response) {
            is Response.Changed -> {
                firestoreDatabase.updateReferenceCollectionTwo(
                    "komentar",
                    modelProduk?.idProduk.toString(),
                    "idKomentar",
                    response.data
                )
                dialog.dismiss()
            }
            is Response.Error -> {
                showLogAssert("error komentar", response.error)
                dialog.dismiss()
            }
            is Response.Success -> {
            }
        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return KomentarViewModel(firestoreDatabase) as T
        }
    }
}