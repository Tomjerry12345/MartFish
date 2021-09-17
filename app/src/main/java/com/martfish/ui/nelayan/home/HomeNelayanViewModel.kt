package com.martfish.ui.nelayan.home

import androidx.lifecycle.*
import com.martfish.database.FirestoreDatabase
import com.martfish.utils.Response
import kotlinx.coroutines.launch

class HomeNelayanViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val data: LiveData<Response> = liveData {
        val response = firestoreDatabase.getAllDataRefeference("produk")
        emit(response)
    }

    val resultSearching =  MutableLiveData<Response>()

    fun onSearching(query: String?) {
        viewModelScope.launch {
            if (query != null) {
                resultSearching.value = firestoreDatabase.getReferenceByQuery("produk", "nama", query)
            }
        }
    }

    fun onByKategory(kategori: String) {
        viewModelScope.launch {
                resultSearching.value = firestoreDatabase.getReferenceByQuery("produk", "kategori", kategori)
        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeNelayanViewModel(firestoreDatabase) as T
        }
    }

}