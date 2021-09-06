package com.martfish.ui.fragment.detail

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelProduk
import com.martfish.utils.Constant
import com.martfish.utils.Response

class DetailProdukViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val namaProduk = MutableLiveData<String>()
    val hargaProduk = MutableLiveData<String>()
    val kecamatan = MutableLiveData<String>()
    val kelurahan = MutableLiveData<String>()
    val alamat = MutableLiveData<String>()
    val stok = MutableLiveData<String>()

    var modelProduk: ModelProduk? = null

    val getKomentar: LiveData<Response> = liveData {
        val response = firestoreDatabase.getReferenceDocumentOne(
            "komentar",
            modelProduk?.idProduk.toString()
        )
        emit(response)
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DetailProdukViewModel(firestoreDatabase) as T
        }
    }

}