package com.martfish.ui.autentikasi.register

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.utils.showSnackbar

class RegisterViewModel : ViewModel() {

    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val namaLengkap = MutableLiveData<String>()
    val jenisAkun = MutableLiveData<String>()
    val kecamatan = MutableLiveData<String>()
    val kelurahan = MutableLiveData<String>()
    val alamat = MutableLiveData<String>()

    fun onRegister(view: View) {
        try {
            val username = username.value ?: throw Exception("Username tidak boleh kosong")
            val password = password.value ?: throw Exception("Password tidak boleh kosong")
            val namaLengkap = namaLengkap.value ?: throw Exception("Nama Lengkap tidak boleh kosong")
            val jenisAkun = jenisAkun.value ?: throw Exception("Jenis akun tidak boleh kosong")
            val kecamatan = kecamatan.value ?: throw Exception("Kecamatan tidak boleh kosong")
            val kelurahan = kelurahan.value ?: throw Exception("Kelurahan tidak boleh kosong")
            val alamat = alamat.value ?: throw Exception("ALamat tidak boleh kosong")

            showSnackbar(view, "Berhasil mendaftar", "success")
        } catch (e: Exception) {
            e.message?.let { showSnackbar(view, it, "error") }
        }
    }

    fun onLogin(view: View) {
        view.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }
}