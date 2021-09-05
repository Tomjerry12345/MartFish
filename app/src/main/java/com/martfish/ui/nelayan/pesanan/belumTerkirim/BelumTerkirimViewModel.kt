package com.martfish.ui.nelayan.pesanan.belumTerkirim

import android.view.View
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import kotlinx.coroutines.launch

class BelumTerkirimViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    private val dataUsers = SavedData.getDataUsers()

    val data: LiveData<Response> = liveData {
        val response = dataUsers?.username?.let {
            firestoreDatabase.getReferenceByTwoQuery("pemesanan", "statusPengiriman", false, "usernamePenjual",
                it
            )
        }

        if (response != null) {
            emit(response)
        }
    }

    fun onPesananTerkirim(idPemesan: String?, view: View) {
        viewModelScope.launch {
            if (idPemesan != null) {
                firestoreDatabase.updateReferenceCollectionOne("pemesanan", idPemesan, "statusPengiriman", true)
            }

            view.findNavController().navigate(R.id.action_belumTerkirimFragment_to_pesananNelayanFragment)
        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BelumTerkirimViewModel(firestoreDatabase) as T
        }
    }
}