package com.martfish.ui.fragment.detail

import androidx.lifecycle.*
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelProduk
import com.martfish.utils.Response

class DetailProdukViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val namaProduk = MutableLiveData<String>()
    val hargaProduk = MutableLiveData<String>()
    val kecamatan = MutableLiveData<String>()
    val kelurahan = MutableLiveData<String>()
    val alamat = MutableLiveData<String>()
    val stok = MutableLiveData<String>()
    val rating = MutableLiveData<String>()

    var modelProduk: ModelProduk? = null

    val getKomentar: LiveData<Response> = liveData {
        val response = firestoreDatabase.getReferenceDocumentOneCollection(
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