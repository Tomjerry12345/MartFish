package com.martfish.ui.fragment.chat

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.martfish.BuildConfig
import com.martfish.R
import com.martfish.database.FirestoreDatabase
import com.martfish.databinding.ChatFragmentBinding
import com.martfish.model.ModelChat
import com.martfish.ui.adapter.ChatAdapter
import com.martfish.utils.*
import java.io.File

class ChatFragment : Fragment(R.layout.chat_fragment) {

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModel.Factory(FirestoreDatabase())
    }

    private lateinit var binding: ChatFragmentBinding

    private val getFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            viewModel.uploadImage(uri, requireView())
        }
    }

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                viewModel.uploadImage(uri, requireView())
            }
        }
    }

    private val cameraAcces = RequestPermission(requireActivity(), Manifest.permission.CAMERA,
        onDenied = { showSnackbar(requireView(), "Permission Denied", "error") },
        onShowRationale = {showSnackbar(requireView(), "Should show Rationale", "error") })

    private var latestTmpUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ChatFragmentBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        getPesan()

        binding.setImage.setOnClickListener {
            showDialogPick()
        }
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

    private fun showDialogPick() {
        val items = arrayOf("Camera", "Gallery")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pilih aksi")
            .setItems(items) { dialog, which ->
                if (which == 0)
                    capturePhoto()
                else
                    getImageFromStorage()
            }
            .show()
    }

    private fun capturePhoto() {
        cameraAcces.runWithPermission {
            lifecycleScope.launchWhenStarted {
                getTmpFileUri().let { uri ->
                    latestTmpUri = uri
                    takeImageResult.launch(uri)
                }
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png", requireContext().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext(), "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    private fun getImageFromStorage() {
        getFromGallery.launch("image/*")
    }

}