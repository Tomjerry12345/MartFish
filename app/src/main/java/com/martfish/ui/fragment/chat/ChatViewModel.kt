package com.martfish.ui.fragment.chat

import android.app.AlertDialog
import android.net.Uri
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelChat
import com.martfish.model.ModelPemesanan
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import com.martfish.utils.showDialog
import com.martfish.utils.showLogAssert
import kotlinx.coroutines.launch

class ChatViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val textChat = MutableLiveData<String>()
    var modelPemesanan: ModelPemesanan? = SavedData.getDataPemesanan()
    val dataUsers = SavedData.getDataUsers()

    lateinit var dialog: AlertDialog

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
        dialog = showDialog(view.context, "Sedang di proses...")
        dialog.show()
        viewModelScope.launch {
            val modelChat = ModelChat(
                textChat.value,
                null,
                dataUsers?.namaLengkap,
                dataUsers?.jenisAkun,
                System.currentTimeMillis().toString()
            )
            saveChat(modelChat, view)
            dialog.dismiss()
        }
    }


    fun uploadImage(uri: Uri, view: View) {
        viewModelScope.launch {
            when (val getUrlImage =
                firestoreDatabase.uploadPhoto(uri, "images/produk/")) {
                is Response.Changed -> {
                    val urlImage = getUrlImage.data as String
                    val modelChat = ModelChat(
                        null,
                        urlImage,
                        dataUsers?.namaLengkap,
                        dataUsers?.jenisAkun,
                        System.currentTimeMillis().toString()
                    )

                    saveChat(modelChat, view)

                }

                is Response.Error -> {
                    showLogAssert("error", getUrlImage.error)
                }

                is Response.Success -> {
                }
            }
            dialog.dismiss()
        }
    }

    private suspend fun saveChat(modelChat: ModelChat, view: View) {
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

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChatViewModel(firestoreDatabase) as T
        }
    }
}