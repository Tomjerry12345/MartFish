package com.martfish.ui.fragment.dataPembeli

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.martfish.database.FirestoreDatabase

class DataPembeliViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {
    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DataPembeliViewModel(firestoreDatabase) as T
        }
    }
}