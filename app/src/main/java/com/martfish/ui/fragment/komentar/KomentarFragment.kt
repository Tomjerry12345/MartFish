package com.martfish.ui.fragment.komentar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.KomentarFragmentBinding
import com.martfish.model.ModelProduk
import com.martfish.utils.Constant
import com.martfish.utils.SavedData
import com.martfish.utils.showLogAssert

class KomentarFragment : Fragment(R.layout.komentar_fragment) {

    private val viewModel: KomentarViewModel by viewModels {
        KomentarViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: KomentarFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = KomentarFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val argument = SavedData.getDataProduk()
        viewModel.modelProduk = argument

        binding.acRatingBar.setOnRatingBarChangeListener { ratingBar, fl, b ->
            showLogAssert("rating value", "$fl")
            viewModel.rating = fl.toDouble()
        }
    }

}