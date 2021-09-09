package com.martfish.ui.nelayan.pesanan.belumTerkirim

import android.view.View
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelPemesanan
import com.martfish.services.webServices
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import com.martfish.utils.showLogAssert
import kotlinx.coroutines.launch

class BelumTerkirimViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    private val dataUsers = SavedData.getDataUsers()

    val data: LiveData<Response> = liveData {
        val response = dataUsers?.username?.let {
            firestoreDatabase.getReferenceByTwoQuery(
                "pemesanan", "statusPengiriman", false, "usernamePenjual",
                it
            )
        }

        if (response != null) {
            emit(response)
        }
    }

    fun getIdTransaction() {
        viewModelScope.launch {
            val response = firestoreDatabase.getReferenceByTwoQuery(
                "pemesanan", "statusPengiriman", false, "usernamePenjual",
                dataUsers?.username!!
            )

            when (response) {
                is Response.Changed -> {
                    val data = response.data as QuerySnapshot
                    val data1 = data.toObjects<ModelPemesanan>()
                    data1.forEach {
                        updateStatusPembayaran(it.idTransaction, it.idPemesan)
                    }
                }
                is Response.Error -> {
                }
                is Response.Success -> {
                }
            }
        }

    }

    private fun updateStatusPembayaran(idTransaction: String?, idPemesan: String?) {
        viewModelScope.launch {
            val response = webServices.getStatusTransaction(idTransaction!!)
            val transactionStatus = response.body()?.transactionStatus
            showLogAssert("response status pembayaran", "$transactionStatus")
            if (idPemesan != null) {
                firestoreDatabase.updateReferenceCollectionOne("pemesanan", idPemesan, "statusPembayaran", transactionStatus!!)
            }
        }
    }

    fun onPesananTerkirim(idPemesan: String?, view: View) {
        showLogAssert("idPemesan", "$idPemesan")
        viewModelScope.launch {
            if (idPemesan != null) {
                firestoreDatabase.updateReferenceCollectionOne(
                    "pemesanan",
                    idPemesan,
                    "statusPengiriman",
                    true
                )
                view.findNavController().navigate(R.id.action_pesananNelayanFragment_self)
            }

        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BelumTerkirimViewModel(firestoreDatabase) as T
        }
    }
}