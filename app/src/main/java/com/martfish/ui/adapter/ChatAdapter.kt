package com.martfish.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textview.MaterialTextView
import com.martfish.R
import com.martfish.model.ModelChat
import com.martfish.utils.showLogAssert

class ChatAdapter(private val chat: List<ModelChat>) :
    RecyclerView.Adapter<ModelHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when(chat[position].jenisAkun) {
            "Nelayan" -> 0
            else -> 1
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ModelHolder {
        return when(p1) {
            0 -> {
                ModelHolder(
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat_terkirim, viewGroup, false)
                )
            }

            else -> {
                ModelHolder(
                    LayoutInflater.from(viewGroup.context).inflate(R.layout.item_chat_diterima, viewGroup, false)
                )
            }


        }

    }

    override fun getItemCount(): Int = chat.size

    override fun onBindViewHolder(holder: ModelHolder, position: Int) {
        holder.bindKomentar(chat[position])
    }
}

class ModelHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val imgProduk = view.findViewById<AppCompatImageView>(R.id.imgProduk)
    private val mtvNama = view.findViewById<MaterialTextView>(R.id.mtvNama)
    private val mtvChat = view.findViewById<MaterialTextView>(R.id.mtvChat)

    fun bindKomentar(chat: ModelChat) {
        Glide
            .with(view.context)
            .load(chat.image)
            .apply(RequestOptions().override(200, 200))
            .centerCrop()
            .into(imgProduk);
        mtvNama.text = chat.nama
        mtvChat.text = chat.message
    }
}