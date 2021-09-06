package com.martfish.ui.fragment.chat

import android.view.View
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelChat
import com.martfish.model.ModelPemesanan
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import kotlinx.coroutines.launch

class ChatViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val textChat = MutableLiveData<String>()
    val image = MutableLiveData<String>()

    var modelPemesanan: ModelPemesanan? = SavedData.getDataPemesanan()
    val dataUsers = SavedData.getDataUsers()
    

    val getPesan: LiveData<Response> = liveData {
        val response = firestoreDatabase.getReferenceDocumentThree(
            "chat",
            modelPemesanan?.idProduk.toString(),
            modelPemesanan?.usernamePenjual.toString(),
            modelPemesanan?.usernamePemesan.toString(),
        )
        emit(response)
    }

    fun onSend(view: View) {
        val modelChat = ModelChat(
            textChat.value,
            image.value,
            dataUsers?.namaLengkap,
            dataUsers?.jenisAkun,
            System.currentTimeMillis().toString()
        )
        viewModelScope.launch {
            firestoreDatabase.saveDataReferenceDocumentThree(
                "chat",
                modelPemesanan?.idProduk.toString(),
                modelPemesanan?.usernamePenjual.toString(),
                modelPemesanan?.usernamePemesan.toString(),
                modelChat,
            )

            textChat.value = ""

            if (dataUsers?.jenisAkun == "Nelayan")
                view.findNavController().navigate(R.id.action_chatFragment_self)
            else
                view.findNavController().navigate(R.id.action_chatFragment2_self)
        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChatViewModel(firestoreDatabase) as T
        }
    }
}