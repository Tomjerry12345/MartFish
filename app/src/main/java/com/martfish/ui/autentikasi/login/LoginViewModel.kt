package com.martfish.ui.autentikasi.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.model.ModelUsers
import com.martfish.ui.nelayan.NelayanActivity
import com.martfish.ui.pembeli.PembeliActivity
import com.martfish.utils.*
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class LoginViewModel(
    private val activity: FragmentActivity,
    val firestoreDatabase: FirestoreDatabase
) : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val response = MutableLiveData<Response>()
    lateinit var dialog: AlertDialog

    fun onLogin(view: View) {
        dialog = showDialog(view.context, "Sedang di proses...")
        dialog.show()
        try {
            val username = username.value ?: throw Exception("Username tidak boleh kosong")
            val password = password.value ?: throw Exception("Password tidak boleh kosong")
            validation(username, password)
        } catch (e: Exception) {
            e.message?.let { showSnackbar(view, it, "error") }
            dialog.dismiss()
        }
    }

    private fun validation(username: String, password: String) {
        viewModelScope.launch {
            when (val response1 = firestoreDatabase.getReferenceByQuery("users", "username", username)) {
                is Response.Changed -> {
                    val data: ArrayList<ModelUsers> = response1.data as ArrayList<ModelUsers>
                    showLogAssert("succes", "${response1.data}")

                    if (data.isNotEmpty()) {
                        if (data[0].password == password) {
                            response.postValue(Response.Changed(data[0]))
                            dialog.dismiss()
                        } else {
                            response.postValue(Response.Error("Password salah"))
                            dialog.dismiss()
                        }
                    } else {
                        response.postValue(Response.Error("Username tidak di temukan"))
                        dialog.dismiss()
                    }
                }

                is Response.Error -> {
                    showLogAssert("error", response1.error)
                    dialog.dismiss()
                }

                is Response.Success -> {}
            }

        }
    }

    fun onRegister(view: View) {
        view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    class Factory(private val activity: FragmentActivity, val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LoginViewModel(activity, firestoreDatabase) as T
        }
    }

}


