package com.martfish.ui.autentikasi.register

import android.app.AlertDialog
import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelUsers
import com.martfish.utils.Response
import com.martfish.utils.showDialog
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar
import kotlinx.coroutines.launch

class RegisterViewModel(private val firestoreDatabase: FirestoreDatabase) : ViewModel() {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val namaLengkap = MutableLiveData<String>()
    val jenisAkun = MutableLiveData<String>()
    val kecamatan = MutableLiveData<String>()
    val kelurahan = MutableLiveData<String>()
    val alamat = MutableLiveData<String>()
    val noHp = MutableLiveData<String>()
    val imageUri = MutableLiveData<Uri>()

    val response = MutableLiveData<Response>()
    lateinit var dialog: AlertDialog

    fun onRegister(view: View) {
        dialog = showDialog(view.context, "Sedang di proses...")
        dialog.show()
        try {
            val image = imageUri.value ?: throw Exception("Image tidak boleh kosong")
            val username = username.value ?: throw Exception("Username tidak boleh kosong")
            val password = password.value ?: throw Exception("Password tidak boleh kosong")
            val namaLengkap = namaLengkap.value ?: throw Exception("Nama Lengkap tidak boleh kosong")
            val jenisAkun = jenisAkun.value ?: throw Exception("Jenis akun tidak boleh kosong")
            val kecamatan = kecamatan.value ?: throw Exception("Kecamatan tidak boleh kosong")
            val kelurahan = kelurahan.value ?: throw Exception("Kelurahan tidak boleh kosong")
            val alamat = alamat.value ?: throw Exception("ALamat tidak boleh kosong")
            val noHp = noHp.value ?: throw Exception("Nomor Hp tidak boleh kosong")

            viewModelScope.launch {
                when (val getUsername = firestoreDatabase.getReferenceByQuery("users", "username", username)) {
                    is Response.Changed -> {
                        val responseQuery = getUsername.data as QuerySnapshot
                        val data: List<ModelUsers> = responseQuery.toObjects()
                        showLogAssert("data", "$data")

                        if (data.isEmpty()) {
                            imageUpload(
                                image,
                                username,
                                password,
                                namaLengkap,
                                jenisAkun,
                                kecamatan,
                                kelurahan,
                                alamat,
                                noHp
                            )

                        } else {
                            response.postValue(Response.Error("Username sudah terdaftar"))
                        }
                    }
                    is Response.Error -> {
                        showLogAssert("error", getUsername.error)
                    }
                    is Response.Success -> {
                    }
                }
            }

        } catch (e: Exception) {
            e.message?.let { showSnackbar(view, it, "error") }
            dialog.dismiss()
        }
    }

    private suspend fun imageUpload(
        image: Uri,
        username: String,
        password: String,
        namaLengkap: String,
        jenisAkun: String,
        kecamatan: String,
        kelurahan: String,
        alamat: String,
        noHp: String
    ) {

        when (val getUrlImage = firestoreDatabase.uploadPhoto(image, "images/users/")) {
            is Response.Changed -> {
                val urlImage = getUrlImage.data as String
                val users = ModelUsers(
                    username,
                    password,
                    namaLengkap,
                    jenisAkun,
                    kecamatan,
                    kelurahan,
                    alamat,
                    noHp,
                    urlImage
                )
//                val getResponse = firestoreDatabase.saveDataReference(
//                    "users",
//                    users,
//                    "Pendaftaran berhasil"
//                )
                saveUsers(users)
            }

            is Response.Error -> {
                showLogAssert("error", getUrlImage.error)
            }

            is Response.Success -> {
            }
        }
    }

    private suspend fun saveUsers(users: ModelUsers) {
        val getResponse =
            firestoreDatabase.saveDataReference("users", users, "Pendaftaran berhasil")
        when (getResponse) {
            is Response.Changed -> {
                response.value = firestoreDatabase.updateReferenceCollectionOne(
                    "users",
                    getResponse.data.toString(),
                    "idUsers",
                    getResponse.data.toString()
                )
            }

            is Response.Error -> {
                showLogAssert("error", getResponse.error)
            }

            is Response.Success -> {
            }
        }

        showLogAssert("getResponse", "$getResponse")
        response.postValue(getResponse)
        dialog.dismiss()
    }

    fun onLogin(view: View) {
        view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return RegisterViewModel(firestoreDatabase) as T
        }
    }
}

