package com.martfish.ui.fragment.dataPembeli

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.DataPembeliFragmentBinding

class DataPembeliFragment : Fragment(R.layout.data_pembeli_fragment) {

    private val viewModel: DataPembeliViewModel by viewModels {
        DataPembeliViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: DataPembeliFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataPembeliFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }
}