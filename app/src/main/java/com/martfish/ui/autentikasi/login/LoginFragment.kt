package com.martfish.ui.autentikasi.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.LoginFragmentBinding
import com.martfish.model.ModelUsers
import com.martfish.ui.nelayan.NelayanActivity
import com.martfish.ui.pembeli.PembeliActivity
import com.martfish.utils.*
import com.midtrans.sdk.corekit.utilities.Utils.hideKeyboard





class LoginFragment : Fragment(R.layout.login_fragment) {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory(requireActivity(), FirestoreDatabase())
    }

    private lateinit var binding: LoginFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        SavedData.init(requireActivity())

        viewModel.response.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {}
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(view, result.error, "error")
                }
                is Response.Changed -> {
                    val user = result.data as ModelUsers
                    intentTo(user)
                }
            }
        })

        hideKeyboardEditText()
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager? = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun hideKeyboardEditText() {
        binding.edtUsername.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        binding.edtPasswodrd.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }
    }

    private fun intentTo(users: ModelUsers) {
        val intent = when (users.jenisAkun) {
            Constant.reffNelayan -> {
                Intent(activity, NelayanActivity::class.java)
            }
            else -> {
                Intent(activity, PembeliActivity::class.java)
            }
        }
        SavedData.saveDataUsers(users)
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }

}