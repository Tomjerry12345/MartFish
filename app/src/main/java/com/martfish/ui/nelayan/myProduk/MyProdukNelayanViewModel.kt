package com.martfish.ui.nelayan.myProduk

import androidx.lifecycle.*
import com.martfish.database.FirestoreDatabase
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import com.martfish.utils.showLogAssert
import kotlinx.coroutines.launch

class MyProdukNelayanViewModel(private val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    private val dataUsers = SavedData.getDataUsers()

    val data: LiveData<Response> = liveData {
        if (dataUsers?.username != null) {
            val response = firestoreDatabase.getReferenceByQuery("produk", "usernamePenjual",
                dataUsers.username
            )
            emit(response)
        }
    }

    val response = MutableLiveData<Response>()

    fun btnDelete(idProduk: String?) {
        showLogAssert("terklik btnDelete", "$idProduk")
        viewModelScope.launch {
            val result = firestoreDatabase.deleteReferenceCollectionOne("produk", idProduk!!)
            response.value = result
        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MyProdukNelayanViewModel(firestoreDatabase) as T
        }
    }
}