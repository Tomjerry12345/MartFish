package com.martfish.ui.nelayan.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.martfish.R
import com.martfish.model.ModelProduk

class ProdukAdapter(private val produk: List<ModelProduk>) : RecyclerView.Adapter<ProdukHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ProdukHolder {
        return ProdukHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_card_produk, viewGroup, false))
    }

    override fun getItemCount(): Int = produk.size

    override fun onBindViewHolder(holder: ProdukHolder, position: Int) {
        holder.bindHero(produk[position])
    }
}

class ProdukHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val imgProduk = view.findViewById<AppCompatImageView>(R.id.imgProduk)
    private val mtvTitle = view.findViewById<MaterialTextView>(R.id.mtvTitle)
    private val mtvHarga = view.findViewById<MaterialTextView>(R.id.mtvHarga)
    private val mtvLokasi = view.findViewById<MaterialTextView>(R.id.mtvLokasi)
    private val ratingProduk = view.findViewById<AppCompatRatingBar>(R.id.ratingProduk)


    @SuppressLint("SetTextI18n")
    fun bindHero(produk: ModelProduk) {
        Glide
            .with(view.context)
            .load(produk.image)
            .centerCrop()
            .placeholder(R.mipmap.ic_image_placeholder)
            .into(imgProduk);
        mtvTitle.text = produk.nama
        mtvHarga.text = "Rp. ${produk.harga}"
        mtvLokasi.text = produk.lokasi
        ratingProduk.rating = produk.rating

    }
}