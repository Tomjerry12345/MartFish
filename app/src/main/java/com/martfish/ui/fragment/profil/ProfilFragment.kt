package com.martfish.ui.fragment.profil

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.ProfilFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.model.ModelUsers
import com.martfish.ui.adapter.ProdukAdapter
import com.martfish.ui.autentikasi.AutentikasiActivity
import com.martfish.utils.Response
import com.martfish.utils.SavedData
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar

class ProfilFragment : Fragment(R.layout.profil_fragment) {

    private lateinit var binding: ProfilFragmentBinding

    private val viewModel: ProfilViewModel by viewModels {
        ProfilViewModel.Factory(FirestoreDatabase())
    }

    private val dataUsers = SavedData.getDataUsers()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ProfilFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        getUsers()

        binding.btnEditUser.setOnClickListener {
            if (dataUsers?.jenisAkun == "Nelayan") {
                showLogAssert("msg", "Nelayan")
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            }
            else {
                showLogAssert("msg", "Pembeli")
                findNavController().navigate(R.id.action_profileFragment_to_editProfilFragment2)
            }
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(requireContext(), AutentikasiActivity::class.java)
            requireActivity().startActivity(intent)
            requireActivity().finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getUsers() {
        viewModel.data.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {}
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(requireView(), result.error, "error")
                }
                is Response.Changed -> {
                    val data = result.data as QuerySnapshot
                    showLogAssert("data", "$data")
                    val dataUsers: List<ModelUsers> = data.toObjects()
                    showLogAssert("dataUsers", "$dataUsers")
                    val users = dataUsers[0]

                    Glide.with(requireContext())
                        .load(users.image)
                        .circleCrop()
                        .into(binding.imgImage)

                    binding.mtvUsername.text = users.namaLengkap
                    binding.mtvNomor.text = users.noHp
                    binding.mtvJenisAkun.text = users.jenisAkun
                    binding.mtvNamaLengkap.text = users.namaLengkap
                    binding.mtvKecamatan.text = users.kecamatan
                    binding.mtvKelurahan.text = users.kelurahan
                    binding.mtvAlamat.text = users.alamat
                }
            }
        })
    }

}