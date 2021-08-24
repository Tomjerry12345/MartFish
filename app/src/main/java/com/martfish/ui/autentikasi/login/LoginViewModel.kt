package com.martfish.ui.autentikasi.login

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.ui.nelayan.NelayanActivity
import com.martfish.ui.pembeli.PembeliActivity
import com.martfish.utils.Constant
import com.martfish.utils.showSnackbar

@SuppressLint("StaticFieldLeak")
class LoginViewModel(private val activity: FragmentActivity) : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun onLogin(view: View) {
        try {
            val username = username.value ?: throw Exception("Username tidak boleh kosong")
            val password = password.value ?: throw Exception("Password tidak boleh kosong")
            validation(view, username, password)
        } catch (e: Exception) {
            e.message?.let { showSnackbar(view, it, "error") }
        }
    }

    private fun validation(view: View, username: String, password: String) {
        val intent = when (username) {
            Constant.reffNelayan -> {
                Intent(activity, NelayanActivity::class.java)
            }
           else -> {
                Intent(activity, PembeliActivity::class.java)
            }
        }

        activity.startActivity(intent)
        activity.finish()
    }

    fun onRegister(view: View) {
        view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    class Factory(private val activity: FragmentActivity) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LoginViewModel(activity) as T
        }
    }

}


