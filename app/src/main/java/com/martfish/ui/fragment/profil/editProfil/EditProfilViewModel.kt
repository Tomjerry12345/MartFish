package com.martfish.ui.fragment.profil.editProfil

import android.app.AlertDialog
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelUsers
import com.martfish.utils.Response
import com.martfish.utils.showDialog
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar
import kotlinx.coroutines.launch

class EditProfilViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val dataUsers = MutableLiveData<ModelUsers>()

    val namaLengkap = MutableLiveData<String>()
    val kecamatan = MutableLiveData<String>()
    val kelurahan = MutableLiveData<String>()
    val alamat = MutableLiveData<String>()
    val noHp = MutableLiveData<String>()
    val imageUri = MutableLiveData<Uri>()

    val response = MutableLiveData<Response>()

    lateinit var dialog: AlertDialog

    fun onEdit(view: View) {
        dialog = showDialog(view.context, "Sedang di proses...")
        dialog.show()

        try {
            val namaLengkap =
                namaLengkap.value ?: throw Exception("Nama Lengkap tidak boleh kosong")
            val kecamatan = kecamatan.value ?: throw Exception("Kecamatan tidak boleh kosong")
            val kelurahan = kelurahan.value ?: throw Exception("Kelurahan tidak boleh kosong")
            val alamat = alamat.value ?: throw Exception("ALamat tidak boleh kosong")
            val noHp = noHp.value ?: throw Exception("Nomor Hp tidak boleh kosong")

            val dataUsers = dataUsers.value
                ?: throw Exception("Terjadi kesalahan silahkan restart kembali aplikas")


            viewModelScope.launch {
                if (imageUri.value == null) {
                    val users = ModelUsers(
                        dataUsers.username,
                        dataUsers.password,
                        namaLengkap,
                        dataUsers.jenisAkun,
                        kecamatan,
                        kelurahan,
                        alamat,
                        noHp,
                        dataUsers.image,
                        dataUsers.idUsers,
                    )

                    editUsers(users)

                    dialog.dismiss()
                } else {
                    when (val getUrlImage =
                        firestoreDatabase.uploadPhoto(imageUri.value!!, "images/users/")) {
                        is Response.Changed -> {
                            val urlImage = getUrlImage.data as String

                            val users = ModelUsers(
                                dataUsers.username,
                                dataUsers.password,
                                namaLengkap,
                                dataUsers.jenisAkun,
                                kecamatan,
                                kelurahan,
                                alamat,
                                noHp,
                                urlImage,
                                dataUsers.idUsers,
                            )

                            editUsers(users)

                            dialog.dismiss()

                        }

                        is Response.Error -> {
                            showLogAssert("error", getUrlImage.error)
                            dialog.dismiss()
                        }

                        is Response.Success -> {
                        }
                    }
                }

            }

        } catch (e: Exception) {
            e.message?.let { showSnackbar(view, it, "error") }
            dialog.dismiss()
        }
    }

    suspend fun editUsers(users: ModelUsers) {
        response.value = dataUsers.value?.idUsers?.let {
            firestoreDatabase.updateReferenceCollectionOne(
                "users",
                it, null, users
            )
        }
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditProfilViewModel(firestoreDatabase) as T
        }
    }
}