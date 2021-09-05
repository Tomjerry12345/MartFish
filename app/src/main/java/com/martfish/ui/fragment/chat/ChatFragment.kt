package com.martfish.ui.fragment.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.ChatFragmentBinding
import com.martfish.model.ModelPemesanan
import com.martfish.utils.Constant
import com.martfish.utils.showLogAssert

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

        val argument = arguments?.getParcelable<ModelPemesanan>(Constant.pemesananBundle)

        showLogAssert("chat", "$argument")
    }

}