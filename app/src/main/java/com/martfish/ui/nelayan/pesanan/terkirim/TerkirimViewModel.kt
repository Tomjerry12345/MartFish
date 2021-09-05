package com.martfish.ui.nelayan.pesanan.terkirim

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.martfish.database.FirestoreDatabase
import com.martfish.utils.Response

class TerkirimViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val data: LiveData<Response> = liveData {
        val response = firestoreDatabase.getReferenceByQuery("pemesanan", "statusPengiriman", true)
        emit(response)
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TerkirimViewModel(firestoreDatabase) as T
        }
    }
}