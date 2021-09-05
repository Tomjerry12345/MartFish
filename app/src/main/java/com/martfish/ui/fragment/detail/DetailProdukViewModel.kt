package com.martfish.ui.fragment.detail

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase

class DetailProdukViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val namaProduk = MutableLiveData<String>()
    val hargaProduk = MutableLiveData<String>()
    val kecamatan = MutableLiveData<String>()
    val kelurahan = MutableLiveData<String>()
    val alamat = MutableLiveData<String>()
    val stok = MutableLiveData<String>()

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DetailProdukViewModel(firestoreDatabase) as T
        }
    }

}