package com.martfish.ui.fragment.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.martfish.database.FirestoreDatabase

class DetailProdukViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DetailProdukViewModel(firestoreDatabase) as T
        }
    }

}