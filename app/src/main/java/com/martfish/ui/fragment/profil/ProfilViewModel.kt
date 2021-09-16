package com.martfish.ui.fragment.profil

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.martfish.database.FirestoreDatabase
import com.martfish.utils.Response
import com.martfish.utils.SavedData

class ProfilViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val dataUsers = SavedData.getDataUsers()

    val data: LiveData<Response> = liveData {
        val response =
            dataUsers?.username?.let {
                firestoreDatabase.getReferenceByQuery(
                    "users",
                    "username",
                    it
                )
            }

        if (response != null) {
            emit(response)
        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ProfilViewModel(firestoreDatabase) as T
        }
    }
}