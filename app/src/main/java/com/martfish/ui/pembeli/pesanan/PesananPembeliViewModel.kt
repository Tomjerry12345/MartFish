package com.martfish.ui.pembeli.pesanan

import androidx.lifecycle.*
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelPemesanan
import com.martfish.services.webServices
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import com.martfish.utils.Time
import com.martfish.utils.showLogAssert
import kotlinx.coroutines.launch

class PesananPembeliViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    private val dataUsers = SavedData.getDataUsers()

    val data: LiveData<Response> = liveData {
        val response = dataUsers?.username?.let {
            firestoreDatabase.getReferenceByQuery("pemesanan", "usernamePemesan",
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
                "pemesanan", "statusPengiriman", false, "usernamePemesan",
                dataUsers?.username!!
            )

            when (response) {
                is Response.Changed -> {
                    val data = response.data as QuerySnapshot
                    val data1 = data.toObjects<ModelPemesanan>()
                    data1.forEach {
                        updateStatusPembayaran(it.idTransaction, it.idPemesan, it.expiredJam, it.menit, it.statusPengantaran)
                    }
                }
                is Response.Error -> {
                }
                is Response.Success -> {
                }
            }
        }

    }

    private fun updateStatusPembayaran(
        idTransaction: String?,
        idPemesan: String?,
        expiredJam: Int?,
        menit: Int?,
        statusPengantaran: String?
    ) {
        viewModelScope.launch {
            showLogAssert("hourOfDay", "${Time.hourOfDay}")
            showLogAssert("minuteOfDay", "${Time.minuteOfDay}")
            showLogAssert("statusPengantaran", "$statusPengantaran")
            showLogAssert("expiredJam", "$expiredJam")
            showLogAssert("menit", "$menit")
            if (Time.hourOfDay >= expiredJam!! && Time.minuteOfDay >= menit!! && statusPengantaran != "expired") {

                if (idPemesan != null) {
                    firestoreDatabase.updateReferenceCollectionOne("pemesanan", idPemesan, "statusPengantaran", "expired")
                }
            }
            if (idTransaction != null) {
                val response = webServices.getStatusTransaction(idTransaction)
                val transactionStatus = response.body()?.transactionStatus

                showLogAssert("response status pembayaran", "$transactionStatus")
                if (idPemesan != null && transactionStatus != null) {
                    firestoreDatabase.updateReferenceCollectionOne("pemesanan", idPemesan, "statusPembayaran", transactionStatus)
                }
            }

        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PesananPembeliViewModel(firestoreDatabase) as T
        }
    }

}