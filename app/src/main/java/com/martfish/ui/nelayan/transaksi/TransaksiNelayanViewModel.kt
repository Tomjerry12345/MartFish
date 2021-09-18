package com.martfish.ui.nelayan.transaksi

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.martfish.database.FirestoreDatabase
import com.martfish.utils.Response
import com.martfish.utils.SavedData

class TransaksiNelayanViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    private val dataUsers = SavedData.getDataUsers()

    val data: LiveData<Response> = liveData {
        val response = dataUsers?.username?.let {
            firestoreDatabase.getReferenceByTwoQueryThreeValue(
                "pemesanan",
                "usernamePenjual",
                it,
                "statusPembayaran",
                "settlement",
                ""
            )
        }

        if (response != null) {
            emit(response)
        }
    }

//    val data1: LiveData<Response> = liveData {
//        val response = dataUsers?.username?.let {
//            firestoreDatabase.getReferenceByTwoQuery("pemesanan", "usernamePenjual", it, "statusPembayaran", null)
//        }
//
//        if (response != null) {
//            emit(response)
//        }
//    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TransaksiNelayanViewModel(firestoreDatabase) as T
        }
    }


}