package com.martfish.ui.autentikasi.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.LoginFragmentBinding
import com.martfish.model.ModelUsers
import com.martfish.ui.nelayan.NelayanActivity
import com.martfish.ui.pembeli.PembeliActivity
import com.martfish.utils.*

class LoginFragment : Fragment(R.layout.login_fragment) {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory(requireActivity(), FirestoreDatabase())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = LoginFragmentBinding.bind(view)
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