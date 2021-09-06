package com.martfish.ui.fragment.komentar

import android.app.AlertDialog
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelKomentar
import com.martfish.model.ModelProduk
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import com.martfish.utils.showDialog
import kotlinx.coroutines.launch

class KomentarViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {
    val komentar = MutableLiveData<String>()

    var modelProduk: ModelProduk? = null
    val dataUsers = SavedData.getDataUsers()

    lateinit var dialog: AlertDialog

    fun sendKomentar(view: View) {

        dialog = showDialog(view.context, "Sedang di proses...")
        dialog.show()

        val komentar1 = ModelKomentar(
            null,
            null,
            dataUsers?.namaLengkap,
            komentar.value
        )
        viewModelScope.launch {
            val response = firestoreDatabase.saveDataReferenceDocumentOne("komentar",
                modelProduk?.idProduk.toString(), komentar1)

            when (response) {
                is Response.Changed -> {
                    firestoreDatabase.updateReferenceCollectionTwo("komentar", modelProduk?.idProduk.toString(), "idKomentar", response.data)
                    dialog.dismiss()
                }
                is Response.Error -> {}
                is Response.Success -> {}
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