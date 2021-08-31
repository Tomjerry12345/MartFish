package com.martfish.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.martfish.R
import com.martfish.model.ModelKomentar

class KomentarAdapter(private val komentar: List<ModelKomentar>) :
    RecyclerView.Adapter<KomentarHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): KomentarHolder {
        return KomentarHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_komentar, viewGroup, false)
        )
    }

    override fun getItemCount(): Int = komentar.size

    override fun onBindViewHolder(holder: KomentarHolder, position: Int) {
        holder.bindKomentar(komentar[position])
    }
}

class KomentarHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val imgProduk = view.findViewById<AppCompatImageView>(R.id.imgProduk)
    private val mtvNama = view.findViewById<MaterialTextView>(R.id.mtvNama)
    private val mtvKomentar = view.findViewById<MaterialTextView>(R.id.mtvKomentar)

    fun bindKomentar(komentar: ModelKomentar) {
        Glide
            .with(view.context)
            .load(komentar.image)
            .centerCrop()
            .placeholder(R.mipmap.ic_image_placeholder)
            .into(imgProduk);
        mtvNama.text = komentar.nama
        mtvKomentar.text = komentar.komentar
    }
}