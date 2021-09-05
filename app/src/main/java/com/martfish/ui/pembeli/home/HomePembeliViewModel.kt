package com.martfish.ui.pembeli.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.martfish.database.FirestoreDatabase
import com.martfish.utils.Response

class HomePembeliViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {
    val data: LiveData<Response> = liveData {
        val response = firestoreDatabase.getAllDataRefeference("produk")
        emit(response)
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomePembeliViewModel(firestoreDatabase) as T
        }
    }
}