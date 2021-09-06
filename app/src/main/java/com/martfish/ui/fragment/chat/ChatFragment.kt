package com.martfish.ui.fragment.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.ChatFragmentBinding
import com.martfish.model.ModelChat
import com.martfish.model.ModelPemesanan
import com.martfish.model.ModelProduk
import com.martfish.ui.adapter.ChatAdapter
import com.martfish.ui.adapter.ProdukAdapter
import com.martfish.utils.Constant
import com.martfish.utils.Response
import com.martfish.utils.showLogAssert
import com.martfish.utils.showSnackbar

class ChatFragment : Fragment(R.layout.chat_fragment) {

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: ChatFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        getPesan()
    }

    private fun getPesan() {
        viewModel.getPesan.observe(viewLifecycleOwner, { result ->
            when(result) {
                is Response.Success -> {}
                is Response.Error -> {
                    showLogAssert("error", result.error)
                    showSnackbar(requireView(), result.error, "error")
                }
                is Response.Changed -> {
                    val data = result.data as QuerySnapshot
                    showLogAssert("data", "${data.toObjects<ModelChat>()}")
                    val dataChat = data.toObjects<ModelChat>()

                    val produkAdapter = ChatAdapter(dataChat)
                    binding.rvListChat.apply {
                        layoutManager = LinearLayoutManager(requireActivity()).apply {
                            stackFromEnd = true
                        }
                        adapter = produkAdapter
                    }

                }
            }
        })
    }

}